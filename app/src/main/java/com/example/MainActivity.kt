package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AppDatabase
import com.example.data.repository.HaitouRepository
import com.example.ui.MainViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.screen.ApplyHistoryFeedbackScreen
import com.example.ui.screen.IntelligentJobsScreen
import com.example.ui.screen.ResumeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init Room Database and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HaitouRepository(database)

        // Instantiate ViewModel
        val viewModel: MainViewModel by viewModels { ViewModelFactory(repository) }

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(viewModel)
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(bottom = 80.dp) // Bottom padding safe-frame for NavigationBar
                    ) {
                        val currentTab by viewModel.selectedTab.collectAsState()

                        when (currentTab) {
                            0 -> ResumeScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                            1 -> IntelligentJobsScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                            2 -> ApplyHistoryFeedbackScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(viewModel: MainViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    NavigationBar(
        modifier = Modifier.testTag("app_navigation_bar"),
        containerColor = if (isDark) Color(0xFF18181C) else Color(0xFFF3F3FA),
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { viewModel.selectTab(0) },
            icon = { Icon(Icons.Default.AccountBox, contentDescription = "我的简历") },
            label = { Text("我的简历", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("nav_tab_resume")
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { viewModel.selectTab(1) },
            icon = { Icon(Icons.Default.Work, contentDescription = "智能匹配") },
            label = { Text("智能匹配", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("nav_tab_jobs")
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { viewModel.selectTab(2) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = "海投反馈") },
            label = { Text("海投反馈", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("nav_tab_feedbacks")
        )
    }
}
