package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.ApplicationFeedbackEntity
import com.example.data.entity.JobListingEntity
import com.example.data.entity.ResumeEntity
import com.example.data.repository.HaitouRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SeaApplyProgress(
    val current: Int,
    val total: Int,
    val currentCompanyName: String = "",
    val statusText: String = ""
)

class MainViewModel(private val repository: HaitouRepository) : ViewModel() {

    // Custom navigation: 0 = Resume, 1 = Intelligent Jobs, 2 = Sea-Apply Feedbacks
    private val _selectedTab = MutableStateFlow(1) // Default to Intelligent Jobs so they see the actions immediately
    val selectedTab = _selectedTab.asStateFlow()

    // Key status helper
    val isApiKeyAvailable = repository.isApiKeyConfigured()

    // Master Resume representation
    val resumeState: StateFlow<ResumeEntity?> = repository.resumeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Recommended/Scanned Jobs listings table
    val jobsState: StateFlow<List<JobListingEntity>> = repository.jobsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Feedback Application History
    val feedbacksState: StateFlow<List<ApplicationFeedbackEntity>> = repository.feedbacksFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Individual progress states
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _activeApplyingJobId = MutableStateFlow<Int?>(null)
    val activeApplyingJobId = _activeApplyingJobId.asStateFlow()

    private val _seaApplyProgress = MutableStateFlow<SeaApplyProgress?>(null)
    val seaApplyProgress = _seaApplyProgress.asStateFlow()

    private val _scanError = MutableStateFlow<String?>(null)
    val scanError = _scanError.asStateFlow()

    init {
        // Initialize resume if not yet created in the DB
        viewModelScope.launch {
            repository.getOrInitResume()
            // Scan first-run jobs automatically if list is empty
            delay(300)
            if (repository.jobsFlow.stateIn(this).value.isEmpty()) {
                scanForJobs()
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun clearScanError() {
        _scanError.value = null
    }

    // Triggers "Automatic discovery / scan" based on search preferences
    fun scanForJobs() {
        viewModelScope.launch {
            _isScanning.value = true
            _scanError.value = null
            val result = repository.scanJobsForPreferences()
            _isScanning.value = false
            if (result.isFailure) {
                _scanError.value = result.exceptionOrNull()?.message ?: "检索岗位失败"
            }
        }
    }

    // Handles pasting manual job description manually (e.g. from Boss直聘)
    fun addManualJobFeed(company: String, title: String, salary: String, city: String, jd: String) {
        viewModelScope.launch {
            val job = JobListingEntity(
                companyName = company.ifBlank { "手动录入企业" },
                title = title.ifBlank { "求职岗位" },
                salary = salary.ifBlank { "面议" },
                location = city.ifBlank { "异地" },
                description = jd.ifBlank { "未指定岗位职责" },
                source = "手动投喂"
            )
            repository.insertJob(job)
        }
    }

    // Individual standard AI Optimize & Apply action
    fun optimizeAndApplyIndividual(job: JobListingEntity) {
        viewModelScope.launch {
            _activeApplyingJobId.value = job.id
            val result = repository.optimizeAndApply(job)
            _activeApplyingJobId.value = null
            if (result.isSuccess) {
                // Auto switch to feedbacks tab to let them view the custom result instantly
                _selectedTab.value = 2
            }
        }
    }

    // Sea-Apply (一键海投): automatically loops over all loaded jobs, optimizes, applies, and renders feedback live!
    fun triggerOneClickSeaApply() {
        viewModelScope.launch {
            val loadedJobs = repository.jobsFlow.stateIn(this).value
            if (loadedJobs.isEmpty()) {
                _scanError.value = "没有可用岗位！请先点击重新检索生成岗位一览。"
                return@launch
            }

            _seaApplyProgress.value = SeaApplyProgress(0, loadedJobs.size, "", "启动智能海投引擎...")
            delay(1000)

            loadedJobs.forEachIndexed { index, job ->
                _seaApplyProgress.value = SeaApplyProgress(
                    current = index + 1,
                    total = loadedJobs.size,
                    currentCompanyName = job.companyName,
                    statusText = "正在基于AI优化针对「${job.title}」的专属简历与求职信..."
                )
                // Short buffer to make the progression organic and legible for the user
                delay(1200)

                // Trigger AI optimizing and applying
                val result = repository.optimizeAndApply(job)

                val scoreText = if (result.isSuccess) "契合度分析: ${result.getOrNull()?.matchScore}%" else "匹配投递完成"
                _seaApplyProgress.value = SeaApplyProgress(
                    current = index + 1,
                    total = loadedJobs.size,
                    currentCompanyName = job.companyName,
                    statusText = "「${job.companyName}」匹配成功！$scoreText | 正在自动注入简历库并发送..."
                )
                delay(1000)
            }

            _seaApplyProgress.value = SeaApplyProgress(
                current = loadedJobs.size,
                total = loadedJobs.size,
                currentCompanyName = "一键海投完成",
                statusText = "恭喜！海投引擎已完成所有岗位投递，AI简历及投递反馈已归档在「海投反馈」中。"
            )
            delay(3000)
            _seaApplyProgress.value = null
            // Switch to feedbacks tab so they review
            _selectedTab.value = 2
        }
    }

    // Resume profile modifications savings
    fun updateResume(
        name: String,
        phone: String,
        email: String,
        targetPos: String,
        city: String,
        minSalary: Int,
        skills: String,
        experience: String,
        projects: String,
        education: String
    ) {
        viewModelScope.launch {
            val current = repository.getOrInitResume()
            val updated = current.copy(
                name = name,
                contactPhone = phone,
                contactEmail = email,
                targetPosition = targetPos,
                expectedCity = city,
                minSalary = minSalary,
                skills = skills,
                experience = experience,
                projects = projects,
                education = education
            )
            repository.saveResume(updated)
        }
    }

    // Remove single feedback card from timeline
    fun deleteFeedback(id: Int) {
        viewModelScope.launch {
            repository.deleteFeedback(id)
        }
    }

    // Reset everything
    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllFeedbacks()
            repository.deleteAllJobs()
            scanForJobs()
        }
    }
}

// Simple Factory to build the stateful ViewModel correctly
class ViewModelFactory(private val repository: HaitouRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
