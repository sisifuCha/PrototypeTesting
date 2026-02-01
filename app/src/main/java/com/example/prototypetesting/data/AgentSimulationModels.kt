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
        cursorSpeed = 800L,
        hasJitter = false,
        typeSpeed = 300L
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
        cursorSpeed = 400L,
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
    data class SelectItem(val index: Int) : SimulationAction()  // 选中商品
    data class ShowMenu(val show: Boolean) : SimulationAction() // 显示菜单
    data class ToggleManageMode(val enabled: Boolean) : SimulationAction() // 切换管理模式
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

    // 银发族剧本 - 购物车删除场景
    // 界面布局：顶部栏(0~0.09), 商品1(0.12~0.27), 商品2(0.29~0.44), 商品3(0.46~0.61), 底部栏(0.85~1.0)
    val elderlyScript = SimulationScript(
        persona = AgentPersona.ELDERLY,
        actions = listOf(
            SimulationAction.Wait(800L),
            // 1. 先点击商品1，想找删除按钮
            SimulationAction.MoveTo(Offset(0.5f, 0.20f), "商品1"),
            SimulationAction.Click("商品1"),
            SimulationAction.Wait(1000L),
            // 2. 再点击商品2
            SimulationAction.MoveTo(Offset(0.5f, 0.36f), "商品2"),
            SimulationAction.Click("商品2"),
            SimulationAction.Wait(800L),
            // 3. 尝试点击价格区域
            SimulationAction.MoveTo(Offset(0.75f, 0.22f), "价格区域"),
            SimulationAction.Click("价格区域"),
            SimulationAction.Wait(600L),
            // 4. 终于发现右上角有个菜单按钮
            SimulationAction.MoveTo(Offset(0.92f, 0.05f), "更多菜单"),
            SimulationAction.Wait(500L),
            SimulationAction.Click("更多菜单"),
            SimulationAction.ShowMenu(true),
            SimulationAction.Wait(800L),
            // 5. 点击管理购物车（菜单项位置）
            SimulationAction.MoveTo(Offset(0.78f, 0.13f), "管理购物车"),
            SimulationAction.Click("管理购物车"),
            SimulationAction.ShowMenu(false),
            SimulationAction.ToggleManageMode(true),
            SimulationAction.Wait(600L),
            // 6. 选中要删除的商品（选择框在左侧）
            SimulationAction.MoveTo(Offset(0.12f, 0.20f), "选择框1"),
            SimulationAction.Click("选择框1"),
            SimulationAction.SelectItem(0),
            SimulationAction.Wait(500L),
            // 7. 点击删除按钮（底部栏）
            SimulationAction.MoveTo(Offset(0.5f, 0.92f), "删除按钮"),
            SimulationAction.Click("删除按钮"),
            SimulationAction.ShowToast("删除成功", false),
            SimulationAction.Wait(1000L)
        ),
        thinkingLogs = listOf(
            ThinkingLog("目标：删除购物车中不需要的商品", ThinkingLogType.DECISION),
            ThinkingLog("正在扫描界面...", ThinkingLogType.OBSERVATION),
            ThinkingLog("点击商品，寻找删除入口", ThinkingLogType.DECISION),
            ThinkingLog("没有发现删除按钮", ThinkingLogType.OBSERVATION),
            ThinkingLog("情感状态: 疑惑 (Confused)", ThinkingLogType.EMOTION),
            ThinkingLog("尝试点击其他商品", ThinkingLogType.DECISION),
            ThinkingLog("仍未发现删除功能", ThinkingLogType.OBSERVATION),
            ThinkingLog("情感状态: 困惑加深", ThinkingLogType.EMOTION),
            ThinkingLog("开始探索界面其他区域...", ThinkingLogType.DECISION),
            ThinkingLog("发现右上角有个菜单图标", ThinkingLogType.OBSERVATION),
            ThinkingLog("点击菜单查看选项", ThinkingLogType.DECISION),
            ThinkingLog("发现\"管理购物车\"选项！", ThinkingLogType.OBSERVATION),
            ThinkingLog("情感状态: 豁然开朗", ThinkingLogType.EMOTION),
            ThinkingLog("进入管理模式", ThinkingLogType.DECISION),
            ThinkingLog("出现了选择框，可以勾选商品", ThinkingLogType.OBSERVATION),
            ThinkingLog("勾选要删除的商品", ThinkingLogType.DECISION),
            ThinkingLog("点击底部删除按钮", ThinkingLogType.DECISION),
            ThinkingLog("删除成功！任务完成", ThinkingLogType.OBSERVATION),
            ThinkingLog("情感状态: 满意 (Satisfied)", ThinkingLogType.EMOTION)
        ),
        isSuccess = true
    )

    // 年轻人剧本 - 快速完成
    val youthScript = SimulationScript(
        persona = AgentPersona.YOUTH,
        actions = listOf(
            SimulationAction.Wait(300L),
            SimulationAction.MoveTo(Offset(0.92f, 0.05f), "更多菜单"),
            SimulationAction.Click("更多菜单"),
            SimulationAction.ShowMenu(true),
            SimulationAction.Wait(300L),
            SimulationAction.MoveTo(Offset(0.78f, 0.13f), "管理购物车"),
            SimulationAction.Click("管理购物车"),
            SimulationAction.ShowMenu(false),
            SimulationAction.ToggleManageMode(true),
            SimulationAction.Wait(200L),
            SimulationAction.MoveTo(Offset(0.12f, 0.20f), "选择框1"),
            SimulationAction.Click("选择框1"),
            SimulationAction.SelectItem(0),
            SimulationAction.Wait(200L),
            SimulationAction.MoveTo(Offset(0.5f, 0.92f), "删除按钮"),
            SimulationAction.Click("删除按钮"),
            SimulationAction.ShowToast("删除成功", false),
            SimulationAction.Wait(500L)
        ),
        thinkingLogs = listOf(
            ThinkingLog("目标：删除购物车商品", ThinkingLogType.DECISION),
            ThinkingLog("直接找菜单入口", ThinkingLogType.DECISION),
            ThinkingLog("进入管理模式", ThinkingLogType.DECISION),
            ThinkingLog("勾选并删除", ThinkingLogType.DECISION),
            ThinkingLog("完成", ThinkingLogType.OBSERVATION)
        ),
        isSuccess = true
    )

    fun getScript(persona: AgentPersona): SimulationScript {
        return when (persona) {
            AgentPersona.ELDERLY -> elderlyScript
            AgentPersona.YOUTH -> youthScript
            AgentPersona.PROFESSIONAL -> youthScript
        }
    }
}
