package com.example.prototypetesting.data

import androidx.compose.ui.geometry.Offset

/**
 * Agent 仿真模块数据模型
 * "木偶戏" - 模拟不同用户群体操作 UI 的过程
 */

// Agent 类型（用户画像）
enum class AgentPersona(
    val displayName: String,
    val icon: String,
    val description: String,
    val cursorSpeed: Long,      // 光标移动时长 (ms)
    val hasJitter: Boolean,     // 是否有抖动
    val typeSpeed: Long         // 打字速度 (ms/字符)
) {
    ELDERLY(
        displayName = "银发族",
        icon = "\uD83D\uDC74",
        description = "60岁以上，对数字产品不熟悉",
        cursorSpeed = 2000L,
        hasJitter = true,
        typeSpeed = 500L
    ),
    YOUTH(
        displayName = "数字原住民",
        icon = "\uD83D\uDC66",
        description = "18-25岁，熟练使用各类App",
        cursorSpeed = 300L,
        hasJitter = false,
        typeSpeed = 80L
    ),
    PROFESSIONAL(
        displayName = "职场白领",
        icon = "\uD83D\uDC69\u200D\uD83D\uDCBC",
        description = "追求效率，注重用户体验",
        cursorSpeed = 500L,
        hasJitter = false,
        typeSpeed = 120L
    )
}

// 仿真动作类型
sealed class SimulationAction {
    data class MoveTo(val target: Offset, val targetName: String) : SimulationAction()
    data class Click(val targetName: String) : SimulationAction()
    data class TypeText(val text: String, val targetName: String) : SimulationAction()
    data class Wait(val durationMs: Long) : SimulationAction()
    data class ShowToast(val message: String, val isError: Boolean = false) : SimulationAction()
    object Jitter : SimulationAction()
}

// 思维链日志
data class ThinkingLog(
    val content: String,
    val type: ThinkingLogType = ThinkingLogType.OBSERVATION
)

enum class ThinkingLogType {
    OBSERVATION,    // 观察
    ANALYSIS,       // 分析
    EMOTION,        // 情感状态
    DECISION        // 决策
}

// 仿真剧本
data class SimulationScript(
    val persona: AgentPersona,
    val actions: List<SimulationAction>,
    val thinkingLogs: List<ThinkingLog>,
    val isSuccess: Boolean
)

// 预定义剧本
object SimulationScripts {

    // 银发族剧本 - 失败场景
    val elderlyScript = SimulationScript(
        persona = AgentPersona.ELDERLY,
        actions = listOf(
            SimulationAction.Wait(1000L),
            SimulationAction.MoveTo(Offset(0.5f, 0.3f), "用户名输入框"),
            SimulationAction.Jitter,
            SimulationAction.Click("用户名输入框"),
            SimulationAction.Wait(800L),
            SimulationAction.TypeText("zhang", "用户名输入框"),
            SimulationAction.Wait(1500L),
            SimulationAction.MoveTo(Offset(0.5f, 0.45f), "密码输入框"),
            SimulationAction.Jitter,
            SimulationAction.Click("密码输入框"),
            SimulationAction.Wait(600L),
            // 没有输入密码，直接点击登录
            SimulationAction.MoveTo(Offset(0.5f, 0.62f), "登录按钮"),
            SimulationAction.Jitter,
            SimulationAction.Wait(1200L),
            SimulationAction.Click("登录按钮"),
            SimulationAction.ShowToast("错误：未检测到密码输入", true),
            SimulationAction.Wait(2000L)
        ),
        thinkingLogs = listOf(
            ThinkingLog("正在扫描界面元素...", ThinkingLogType.OBSERVATION),
            ThinkingLog("字体大小 12sp < 认知阈值 16sp", ThinkingLogType.ANALYSIS),
            ThinkingLog("输入框边界不够明显", ThinkingLogType.OBSERVATION),
            ThinkingLog("尝试点击用户名区域", ThinkingLogType.DECISION),
            ThinkingLog("手指精确度下降，需要调整位置", ThinkingLogType.OBSERVATION),
            ThinkingLog("逐字输入中...", ThinkingLogType.OBSERVATION),
            ThinkingLog("未发现明确的引导标识", ThinkingLogType.ANALYSIS),
            ThinkingLog("情感状态: 焦虑 (Anxiety Level: High)", ThinkingLogType.EMOTION),
            ThinkingLog("跳过密码输入，尝试直接登录", ThinkingLogType.DECISION),
            ThinkingLog("操作失败，界面反馈不明确", ThinkingLogType.OBSERVATION),
            ThinkingLog("情感状态: 沮丧 (Frustration Level: Critical)", ThinkingLogType.EMOTION),
            ThinkingLog("放弃任务", ThinkingLogType.DECISION)
        ),
        isSuccess = false
    )

    // 年轻人剧本 - 成功场景
    val youthScript = SimulationScript(
        persona = AgentPersona.YOUTH,
        actions = listOf(
            SimulationAction.Wait(300L),
            SimulationAction.MoveTo(Offset(0.5f, 0.3f), "用户名输入框"),
            SimulationAction.Click("用户名输入框"),
            SimulationAction.TypeText("test_user", "用户名输入框"),
            SimulationAction.MoveTo(Offset(0.5f, 0.45f), "密码输入框"),
            SimulationAction.Click("密码输入框"),
            SimulationAction.TypeText("password123", "密码输入框"),
            SimulationAction.MoveTo(Offset(0.5f, 0.62f), "登录按钮"),
            SimulationAction.Click("登录按钮"),
            SimulationAction.Wait(500L),
            SimulationAction.ShowToast("登录成功！", false),
            SimulationAction.Wait(1000L)
        ),
        thinkingLogs = listOf(
            ThinkingLog("界面布局清晰，标准登录表单", ThinkingLogType.OBSERVATION),
            ThinkingLog("快速定位输入区域", ThinkingLogType.DECISION),
            ThinkingLog("输入用户名", ThinkingLogType.OBSERVATION),
            ThinkingLog("切换到密码框", ThinkingLogType.DECISION),
            ThinkingLog("输入密码", ThinkingLogType.OBSERVATION),
            ThinkingLog("情感状态: 平静 (Calm)", ThinkingLogType.EMOTION),
            ThinkingLog("点击登录按钮", ThinkingLogType.DECISION),
            ThinkingLog("任务完成", ThinkingLogType.OBSERVATION)
        ),
        isSuccess = true
    )

    fun getScript(persona: AgentPersona): SimulationScript {
        return when (persona) {
            AgentPersona.ELDERLY -> elderlyScript
            AgentPersona.YOUTH -> youthScript
            AgentPersona.PROFESSIONAL -> youthScript // 复用年轻人剧本
        }
    }
}
