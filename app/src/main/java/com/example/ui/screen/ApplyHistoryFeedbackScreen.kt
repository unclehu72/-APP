package com.example.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ApplicationFeedbackEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyHistoryFeedbackScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val feedbacks by viewModel.feedbacksState.collectAsState()
    var selectedFeedbackForDetail by remember { mutableStateOf<ApplicationFeedbackEntity?>(null) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Timeline Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "海投自动反馈",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 22.sp
                )
                Text(
                    text = "已完成投递的独一无二简历与自动化反馈记录归档",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (feedbacks.isNotEmpty()) {
                IconButton(
                    onClick = { showResetConfirmation = true },
                    modifier = Modifier.testTag("reset_all_data_button")
                ) {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = "清空所有记录",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (feedbacks.isEmpty()) {
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
                        imageVector = Icons.Default.HistoryToggleOff,
                        contentDescription = "无反馈数据",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "暂无海投记录",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "您可以在【智能匹配】板块中选择中意岗位并点击「AI优化项目并投递」，或者触发「一键海投」进行批量AI优化。归档反馈将在本页实时生成展示。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("feedbacks_list"),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(feedbacks) { feedback ->
                    FeedbackTimelineCard(
                        feedback = feedback,
                        onCardClick = { selectedFeedbackForDetail = feedback },
                        onDeleteClick = { viewModel.deleteFeedback(feedback.id) }
                    )
                }
            }
        }
    }

    // --- SCREEN FULL DRILL-DOWN REPORT FEEDBACK DIALOG ---
    if (selectedFeedbackForDetail != null) {
        FeedbackReportDetailDialog(
            feedback = selectedFeedbackForDetail!!,
            onDismiss = { selectedFeedbackForDetail = null }
        )
    }

    // --- RESET ALL DATA DIALOG ---
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("清空全部海投投递历史", fontWeight = FontWeight.Bold) },
            text = { Text("确定要清除所有的自动反馈简历记录与岗位吗？该操作不可撤销。将会自动归宿到基础岗位重新载入。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetConfirmation = false
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("整夜清空", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun FeedbackTimelineCard(
    feedback: ApplicationFeedbackEntity,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateString = remember(feedback.appliedTimestamp) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        sdf.format(Date(feedback.appliedTimestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp), // 16.dp rounding for Professional Polish
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)), // Flat outlined style
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Match Score Left Badge Circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                getScoreColor(feedback.matchScore),
                                getScoreColor(feedback.matchScore).copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${feedback.matchScore}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "拟合度",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Main Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feedback.jobTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "自动投递成功",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = feedback.companyName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "|",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = feedback.salary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "时间",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = dateString,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    // Click to inspect call description
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "查看AI专属报告",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Detail",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Quick Single Delete Bin Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("delete_feedback_button_${feedback.id}")
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "删除该投递记录",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Full page dialog for rendering tailored resumes and custom Cover Letters
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackReportDetailDialog(
    feedback: ApplicationFeedbackEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf(0) } // 0 = Optimizations, 1 = Cover Letter, 2 = Optimised Resume, 3 = Interview
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 620.dp),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = feedback.jobTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${feedback.companyName} • ${feedback.salary}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }

                // Sub tabs selector (M3 Pill shape segment buttons)
                ScrollableTabRow(
                    selectedTabIndex = activeSubTab,
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    val tabs = listOf("优化亮点", "个性求职信", "定制新简历", "面试提问突击")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = activeSubTab == index,
                            onClick = { activeSubTab = index },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (activeSubTab == index) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (activeSubTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeSubTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Scrollable container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(vertical = 4.dp)
                ) {
                    AnimatedContent(
                        targetState = activeSubTab,
                        label = "dialog_subtabs_anim"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> { // Change highlights
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "AI 是如何精心调优这份简历的？",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = feedback.changeHighlights,
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                            1 -> { // Custom Cover Letter
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "个性打招呼信 / 微简历自推：",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Button(
                                            onClick = {
                                                copyToClipboard(context, feedback.coverLetter)
                                                Toast.makeText(context, "打招呼文案已复制！可直接发往Boss或Lagou招聘沟通中。", Toast.LENGTH_SHORT).show()
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("一键复制", fontSize = 11.sp)
                                        }
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                    ) {
                                        Text(
                                            text = feedback.coverLetter,
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                            2 -> { // Optimized Resume Body
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "本次投递独家定制出的专属最优简历：",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                copyToClipboard(context, feedback.optimizedResume)
                                                Toast.makeText(context, "专属定制简历正文复制成功！", Toast.LENGTH_SHORT).show()
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Resume", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("复制简历", fontSize = 11.sp)
                                        }
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Text(
                                            text = feedback.optimizedResume,
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                            lineHeight = 18.sp,
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                            3 -> { // Interview guidance questions
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "AI 预测该岗位核心面试提问及破局招数",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
                                    ) {
                                        Text(
                                            text = feedback.interviewTips,
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.testTag("dismiss_report_button")) {
                Text("关闭报告")
            }
        }
    )
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Haitou AI Text", text)
    clipboard.setPrimaryClip(clip)
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 90 -> Color(0xFF10B981) // High - Emerald Green
        score >= 80 -> Color(0xFF0288D1) // Med High - Ocean Sky Blue
        score >= 70 -> Color(0xFFF59E0B) // Med - Amber Orange
        else -> Color(0xFFEF4444) // Low - Red
    }
}
