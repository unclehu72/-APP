package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ResumeEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val resume by viewModel.resumeState.collectAsState()
    val isApiKeyAvailable = viewModel.isApiKeyAvailable

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var isEditMode by remember { mutableStateOf(false) }

    // Backup edit fields
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var targetPosition by remember { mutableStateOf("") }
    var expectedCity by remember { mutableStateOf("") }
    var minSalary by remember { mutableStateOf("15") }
    var skills by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }

    // Synchronize form fields when resume is loaded
    LaunchedEffect(resume) {
        resume?.let {
            if (!isEditMode) {
                name = it.name
                phone = it.contactPhone
                email = it.contactEmail
                targetPosition = it.targetPosition
                expectedCity = it.expectedCity
                val salaryString = if (it.minSalary <= 0) "15" else it.minSalary.toString()
                minSalary = salaryString
                skills = it.skills
                experience = it.experience
                projects = it.projects
                education = it.education
            }
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "我的基础简历",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "在AI海投前，请在此编辑并补充您的真实底料简历",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        Button(
                            onClick = {
                                viewModel.updateResume(
                                    name = name,
                                    phone = phone,
                                    email = email,
                                    targetPos = targetPosition,
                                    city = expectedCity,
                                    minSalary = minSalary.toIntOrNull() ?: 15,
                                    skills = skills,
                                    experience = experience,
                                    projects = projects,
                                    education = education
                                )
                                isEditMode = false
                                focusManager.clearFocus()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("save_resume_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "保存", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("保存修改", fontSize = 14.sp)
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { isEditMode = true },
                            modifier = Modifier.testTag("edit_resume_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("编辑简历", fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Key Check Alert Alert Board
            if (!isApiKeyAvailable) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "未配置API Key",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "未配置 Gemini API 密钥",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "检测到使用默认占位密钥。在此状态下APP将启用「本地仿真优化投递」模式以供演示。若需体验真实的AI量身定调与海投，请前往 AI Studio 侧边栏的「Secrets」面板中配置 GEMINI_API_KEY 后重新加载平台。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            if (resume == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (isEditMode) {
                    // --- EDIT MODE LAYOUT ---
                    // Basic Information
                    ResumeSectionCard(title = "个人基本信息", icon = Icons.Default.Person) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("真实姓名") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("edit_name"),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("联系电话") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("edit_phone"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                                )
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("电子邮箱") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("edit_email"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                                )
                            }
                        }
                    }

                    // Seeker Target preferences
                    ResumeSectionCard(title = "期望求职意向", icon = Icons.Default.Work) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = targetPosition,
                                onValueChange = { targetPosition = it },
                                label = { Text("意向岗位（e.g. Android开发工程师、产品经理）") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("edit_position"),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = expectedCity,
                                    onValueChange = { expectedCity = it },
                                    label = { Text("期望城市") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("edit_city"),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                )
                                OutlinedTextField(
                                    value = minSalary,
                                    onValueChange = { minSalary = it },
                                    label = { Text("最低要求薪资 (k)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("edit_salary"),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                                )
                            }
                        }
                    }

                    // Professional Skills
                    ResumeSectionCard(title = "核心专业技能", icon = Icons.Default.Build) {
                        OutlinedTextField(
                            value = skills,
                            onValueChange = { skills = it },
                            label = { Text("请分段输入专业技能，作为AI调优对齐的词盘依据") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("edit_skills"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                        )
                    }

                    // Work Experience
                    ResumeSectionCard(title = "工作/实习经历", icon = Icons.Default.Business) {
                        OutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text("请输入公司名称、职位及关键产出等工作经历详情") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .testTag("edit_experience"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                        )
                    }

                    // Project History
                    ResumeSectionCard(title = "核心主导项目", icon = Icons.Default.Code) {
                        OutlinedTextField(
                            value = projects,
                            onValueChange = { projects = it },
                            label = { Text("请输入项目名称、核心技术架构及您作出的技术亮点功劳") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .testTag("edit_projects"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                        )
                    }

                    // Education background
                    ResumeSectionCard(title = "教育背景", icon = Icons.Default.School) {
                        OutlinedTextField(
                            value = education,
                            onValueChange = { education = it },
                            label = { Text("毕业院校、学位、专业及起止时间") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("edit_education"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        )
                    }
                } else {
                    // --- PREVIEW MODE LAYOUT ---
                    // Master Card containing Headshot Placeholder, Name and Contacts
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Avatar Letter Bubble
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.takeOrNull(1) ?: "海",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = "电话",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = phone,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = "邮箱",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Intentions Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("意向岗位", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f))
                                Text(targetPosition, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("期望城市", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f))
                                Text(expectedCity, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("底线月薪", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f))
                                Text("${minSalary}k以上", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }

                    // Content details
                    PreviewSectionCard(title = "核心技能描述", icon = Icons.Default.Build, content = skills)
                    PreviewSectionCard(title = "工作与实习履历", icon = Icons.Default.Business, content = experience)
                    PreviewSectionCard(title = "代表性主导项目", icon = Icons.Default.Code, content = projects)
                    PreviewSectionCard(title = "教育培训背景", icon = Icons.Default.School, content = education)
                }
            }
        }
    }
}

@Composable
fun ResumeSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // 16.dp rounded corners for Professional Polish
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)), // Flat outline style
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun PreviewSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // 16.dp rounded corners for Professional Polish
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)), // Flat outline style
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content.ifBlank { "未填写内容" },
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = if (content.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun String.takeOrNull(n: Int): String? {
    if (this.isEmpty()) return null
    return if (this.length > n) this.substring(0, n) else this
}
