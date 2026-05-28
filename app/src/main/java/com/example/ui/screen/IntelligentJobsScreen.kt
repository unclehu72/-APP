package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.JobListingEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IntelligentJobsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val jobs by viewModel.jobsState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val activeApplyingId by viewModel.activeApplyingJobId.collectAsState()
    val seaApplyProgress by viewModel.seaApplyProgress.collectAsState()
    val scanError by viewModel.scanError.collectAsState()
    val resume by viewModel.resumeState.collectAsState()

    var showAddJobDialog by remember { mutableStateOf(false) }

    LaunchedEffect(scanError) {
        if (scanError != null) {
            // Screen auto resets error after 5s or we let user dismiss
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header spacing/title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "智能职位发现 & 匹配",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "基于 [ ${resume?.expectedCity ?: "北京"} ] 和 [ ${resume?.targetPosition ?: "Android开发工程师"} ] 自动匹配岗位",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalIconButton(
                    onClick = { showAddJobDialog = true },
                    modifier = Modifier.testTag("feed_manual_job_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "手动投喂岗位信息")
                }
            }

            // Quick Info & Sea-Apply CTA Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp), // rounded-3xl in HTML
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI 智能状态",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "简历更新于: 今天",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Middle Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isScanning) "优化中..." else "AI 智能就绪",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "已匹配 ${jobs.size} 个高契合岗并完成智能词频对齐",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }

                        // Spinner status design
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 0.85f },
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 5.dp,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                            Text(
                                text = "85%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Bottom Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerOneClickSeaApply() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(percent = 50), // Fully rounded - pill
                            modifier = Modifier
                                .weight(1.4f)
                                .height(46.dp)
                                .testTag("one_click_apply_all_button"),
                            enabled = !isScanning && seaApplyProgress == null
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = "海投", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("一键智能海投", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.scanForJobs() },
                            shape = RoundedCornerShape(percent = 50),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("rescan_jobs_button"),
                            enabled = !isScanning && seaApplyProgress == null
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "检索", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("重新扫描检索", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Error Message Bar
            if (scanError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            Text(scanError!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        IconButton(onClick = { viewModel.clearScanError() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Jobs dynamic contents
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("海投APP正在全网自动扫描匹配符合您需求的优质岗位...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else if (jobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkOutline,
                            contentDescription = "无岗位",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "未发现匹配岗位",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "请确认您在[我的简历]中正确配置了期望意向、城市或直接点击[重新扫描检索]加载数据。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.scanForJobs() }) {
                            Text("立即扫描推荐")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("job_listings_list"),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(jobs) { job ->
                        JobListingCard(
                            job = job,
                            isApplying = activeApplyingId == job.id,
                            onIndividualApply = { viewModel.optimizeAndApplyIndividual(job) }
                        )
                    }
                }
            }
        }

        // --- ACTIVE SEA APPLY LIVE STATUS DIALOG ---
        if (seaApplyProgress != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.RocketLaunch,
                            contentDescription = "Sea Apply Progress",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "多岗位「智能海投」跑批进行中",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Progress Indicator
                        val progressNormValue = seaApplyProgress!!.run { current.toFloat() / total.toFloat() }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { progressNormValue },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "正在投递: ${seaApplyProgress!!.currentCompanyName.ifBlank { "初始化" }}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${seaApplyProgress!!.current} / ${seaApplyProgress!!.total}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Debug text prompt logs out
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = seaApplyProgress!!.statusText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "AI正在分步骤读取当前底料，智能梳理对齐该目标岗位职责要点，精化词理，并将独一无二的简历快照与自动生成的求职信分发完毕。请稍候...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // --- MANUAL FEED岗位 MANUALLY ADD INTEGRATED ---
        if (showAddJobDialog) {
            var companyName by remember { mutableStateOf("") }
            var positionTitle by remember { mutableStateOf("") }
            var salaryRange by remember { mutableStateOf("") }
            var jobCity by remember { mutableStateOf("") }
            var jobDesc by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddJobDialog = false },
                title = { Text("投喂新岗位信息", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "在此手动录入从Boss直聘或其他软件中看中的特定职位要求。投喂后可直接进行独家AI优化简历并投递。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("招聘企业名称") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_company_input")
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = positionTitle,
                                onValueChange = { positionTitle = it },
                                label = { Text("岗位工种") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("add_title_input")
                            )
                            OutlinedTextField(
                                value = jobCity,
                                onValueChange = { jobCity = it },
                                label = { Text("城市") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("add_city_input")
                            )
                        }
                        OutlinedTextField(
                            value = salaryRange,
                            onValueChange = { salaryRange = it },
                            label = { Text("薪资待遇 (e.g. 25k-35k)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_salary_input")
                        )
                        OutlinedTextField(
                            value = jobDesc,
                            onValueChange = { jobDesc = it },
                            label = { Text("岗位硬核任职细节（粘贴JD）") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("add_desc_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addManualJobFeed(companyName, positionTitle, salaryRange, jobCity, jobDesc)
                            showAddJobDialog = false
                        },
                        modifier = Modifier.testTag("submit_manual_job_button")
                    ) {
                        Text("投喂喂入")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddJobDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun JobListingCard(
    job: JobListingEntity,
    isApplying: Boolean,
    onIndividualApply: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // 16.dp rounding
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Raw 1: Title & Salary line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = job.salary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Raw 2: Company Name, location & tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = job.companyName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = job.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                // Source source label
                SuggestionChip(
                    onClick = {},
                    label = { Text(job.source, fontSize = 10.sp) },
                    modifier = Modifier.height(24.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Dynamic Job JD Expandable body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "任职要求及职责详情：",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "点击收放",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                AnimatedContent(
                    targetState = expanded,
                    label = "jd_dropdown_anim"
                ) { isExpanded ->
                    if (isExpanded) {
                        Text(
                            text = job.description,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        Text(
                            text = job.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Core individual button action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (job.matchScore > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Recommend, contentDescription = "契合预测", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Text(text = "预测契合度 ${job.matchScore}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                Button(
                    onClick = onIndividualApply,
                    enabled = !isApplying,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(percent = 50), // Pill shape for Professional Polish theme
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("individual_apply_button_${job.id}")
                ) {
                    if (isApplying) {
                        SizedCircularProgressIndicator()
                    } else {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "AI优化并投递", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI优化简历并投递", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SizedCircularProgressIndicator() {
    Box(modifier = Modifier.size(14.dp)) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
