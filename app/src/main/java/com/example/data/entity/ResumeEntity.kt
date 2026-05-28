package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resume_table")
data class ResumeEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "张海威",
    val contactPhone: String = "13800138000",
    val contactEmail: String = "zhanghaiwei@email.com",
    val targetPosition: String = "Android开发工程师",
    val expectedCity: String = "北京",
    val minSalary: Int = 15, // in K
    val skills: String = "1. 熟练掌握 Java, Kotlin 语言及面向对象架构设计；\n2. 熟练掌握 Jetpack Compose, Core components, WorkManager;\n3. 精通 Android 性能调优，包括内存抖动、过度绘制、启动速度优化及线程池调度管理；\n4. 熟练使用 Retrofit, OkHttp 核心架构，及 Room/SQLite 数据库技术；\n5. 拥抱敏捷开发，具有出色的团队沟通和快速定位线上故障的能力。",
    val experience: String = "2023.06 - 至今 | 先锋云起科技有限公司 | 移动应用研发部 | 高级 Android 研发\n- 负责核心 SaaS 客户端业务搭建，引入并落地 Kotlin 协程及 StateFlow 反应式数据流重构，项目整体崩溃率降低 40%；\n- 深度参与内存监控方案落地，排查并阻断各类多线程并发及单例持有造成的潜在泄漏，提升启动效率 15%；\n- 优化项目网络请求底层模型，整合 Retrofit 封装与统一异常阻断处理器，统一项目通信网络层。\n\n2021.07 - 2023.05 | 极信互娱科技有限公司 | 游戏客户端部门 | Android 工程师\n- 支撑千万级用户日活 SDK 内核的集成与发布，维护打包流水线的稳定性；\n- 协作开发并封装各类底层 Native 桥接工具，承载游戏与 Android 平台无缝高频数据交互需求。",
    val projects: String = "项目一：智能移动工作台（App Store / 应用宝已上架）\n- 角色：核心开发组长。\n- 描述：企业端高性能 SaaS 自动化协同看板。\n- 主导技术：完全采用 Jetpack Compose 的单 Activity 纯声明式设计。结合 M3 体系定制深色与流光色彩系统，自适应响应宽屏。对大数据图表进行了自制绘制和滑动限帧优化。\n\n项目二：全网即时沟通协作中心 SDK\n- 角色：开发骨干。\n- 描述：承载多端长连接底层推送与心跳检测的实时即时通信模块。\n- 职责：运用 Kotlin 协程处理高频心跳，自定义弱网异常断线重连，对多进程共享数据库做了多处多路复用锁调谐优化，保障高耗电后台能平滑存活并节电。",
    val education: String = "2017.09 - 2021.06 | 北京科技大学 | 计算机科学与技术 | 本科"
)
