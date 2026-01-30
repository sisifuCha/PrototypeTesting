package com.example.prototypetesting.data

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * 演示场景数据单例
 *
 * 核心策略：高保真数据桩 + 过程可视化 + 确定性交互剧本
 *
 * 使用说明：
 * 1. 在演示时，使用预设的图片触发完美效果
 * 2. 非预设图片会显示通用的"处理中 -> 识别失败/通用结果"流程
 * 3. 热区坐标使用百分比（0.0f - 1.0f），方便适配不同屏幕
 *
 * TODO: 演示前需要调整的参数（搜索 "DEMO_CONFIG" 注释）
 */
object DemoScenarioData {

    // ============ DEMO_CONFIG: 热区坐标配置 ============
    // 坐标格式: Rect(left, top, right, bottom) 使用百分比 0.0f - 1.0f
    // 演示前根据实际手绘图调整这些坐标

    /**
     * 登录页热区配置
     * DEMO_CONFIG: 根据实际手绘图调整坐标
     */
    private val loginPageHotspots = listOf(
        Hotspot(
            id = "login_username",
            rect = Rect(0.1f, 0.25f, 0.9f, 0.32f),  // DEMO_CONFIG: 用户名输入框
            type = ComponentType.INPUT,
            label = "用户名输入框",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "login_password",
            rect = Rect(0.1f, 0.35f, 0.9f, 0.42f),  // DEMO_CONFIG: 密码输入框
            type = ComponentType.INPUT,
            label = "密码输入框",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "login_button",
            rect = Rect(0.15f, 0.5f, 0.85f, 0.58f),  // DEMO_CONFIG: 登录按钮
            type = ComponentType.BUTTON,
            label = "登录按钮",
            confidence = generateConfidence(),
            targetScreenId = "home"  // 点击跳转到首页
        ),
        Hotspot(
            id = "login_register",
            rect = Rect(0.3f, 0.65f, 0.7f, 0.70f),  // DEMO_CONFIG: 注册链接
            type = ComponentType.TEXT,
            label = "注册链接",
            confidence = generateConfidence(),
            targetScreenId = null
        )
    )

    /**
     * 首页热区配置
     * DEMO_CONFIG: 根据实际手绘图调整坐标
     */
    private val homePageHotspots = listOf(
        Hotspot(
            id = "home_search",
            rect = Rect(0.05f, 0.08f, 0.95f, 0.14f),  // DEMO_CONFIG: 搜索框
            type = ComponentType.INPUT,
            label = "搜索框",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "home_banner",
            rect = Rect(0.05f, 0.16f, 0.95f, 0.32f),  // DEMO_CONFIG: 轮播图
            type = ComponentType.IMAGE,
            label = "轮播图",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "home_card1",
            rect = Rect(0.05f, 0.35f, 0.48f, 0.55f),  // DEMO_CONFIG: 功能卡片1
            type = ComponentType.CARD,
            label = "功能卡片",
            confidence = generateConfidence(),
            targetScreenId = "detail"  // 点击跳转到详情页
        ),
        Hotspot(
            id = "home_card2",
            rect = Rect(0.52f, 0.35f, 0.95f, 0.55f),  // DEMO_CONFIG: 功能卡片2
            type = ComponentType.CARD,
            label = "功能卡片",
            confidence = generateConfidence(),
            targetScreenId = "detail"
        ),
        Hotspot(
            id = "home_list",
            rect = Rect(0.05f, 0.58f, 0.95f, 0.90f),  // DEMO_CONFIG: 列表区域
            type = ComponentType.LIST,
            label = "内容列表",
            confidence = generateConfidence(),
            targetScreenId = null
        )
    )

    /**
     * 详情页热区配置
     * DEMO_CONFIG: 根据实际手绘图调整坐标
     */
    private val detailPageHotspots = listOf(
        Hotspot(
            id = "detail_back",
            rect = Rect(0.02f, 0.02f, 0.12f, 0.07f),  // DEMO_CONFIG: 返回按钮
            type = ComponentType.BUTTON,
            label = "返回按钮",
            confidence = generateConfidence(),
            targetScreenId = "home"  // 返回首页
        ),
        Hotspot(
            id = "detail_image",
            rect = Rect(0.05f, 0.10f, 0.95f, 0.40f),  // DEMO_CONFIG: 详情图片
            type = ComponentType.IMAGE,
            label = "详情图片",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "detail_title",
            rect = Rect(0.05f, 0.42f, 0.95f, 0.48f),  // DEMO_CONFIG: 标题
            type = ComponentType.TEXT,
            label = "标题文本",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "detail_desc",
            rect = Rect(0.05f, 0.50f, 0.95f, 0.70f),  // DEMO_CONFIG: 描述
            type = ComponentType.TEXT,
            label = "描述文本",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "detail_button",
            rect = Rect(0.1f, 0.85f, 0.9f, 0.93f),  // DEMO_CONFIG: 操作按钮
            type = ComponentType.BUTTON,
            label = "确认按钮",
            confidence = generateConfidence(),
            targetScreenId = null
        )
    )

    /**
     * 通用热区配置（用于非预设图片）
     */
    private val genericHotspots = listOf(
        Hotspot(
            id = "generic_1",
            rect = Rect(0.1f, 0.15f, 0.9f, 0.22f),
            type = ComponentType.INPUT,
            label = "输入框",
            confidence = generateConfidence(),
            targetScreenId = null
        ),
        Hotspot(
            id = "generic_2",
            rect = Rect(0.2f, 0.45f, 0.8f, 0.53f),
            type = ComponentType.BUTTON,
            label = "按钮",
            confidence = generateConfidence(),
            targetScreenId = null
        )
    )

    // ============ 页面配置映射 ============

    /**
     * 演示页面数据
     * 索引对应上传的图片顺序: 0=登录页, 1=首页, 2=详情页
     */
    val demoPages = listOf(
        DemoPage(
            id = "login",
            name = "登录页",
            hotspots = loginPageHotspots
        ),
        DemoPage(
            id = "home",
            name = "首页",
            hotspots = homePageHotspots
        ),
        DemoPage(
            id = "detail",
            name = "详情页",
            hotspots = detailPageHotspots
        )
    )

    /**
     * 根据页面索引获取热区数据
     * @param pageIndex 页面索引（0开始）
     * @return 对应页面的热区列表，超出范围返回通用热区
     */
    fun getHotspotsForPage(pageIndex: Int): List<Hotspot> {
        return if (pageIndex in demoPages.indices) {
            demoPages[pageIndex].hotspots
        } else {
            genericHotspots
        }
    }

    /**
     * 根据页面ID获取页面索引
     */
    fun getPageIndexById(pageId: String): Int {
        return demoPages.indexOfFirst { it.id == pageId }.takeIf { it >= 0 } ?: 0
    }

    // ============ AI 识别过程模拟文案 ============

    /**
     * 扫描过程显示的文案（按顺序显示）
     */
    val scanningMessages = listOf(
        ScanMessage("正在初始化 PyTorch 运行时...", 400),
        ScanMessage("加载 R-CNN 预训练模型...", 600),
        ScanMessage("执行图像预处理 (1024×1024)...", 300),
        ScanMessage("运行多尺度特征金字塔网络...", 500),
        ScanMessage("R-CNN 边缘检测中...", 700),
        ScanMessage("应用注意力机制优化...", 400),
        ScanMessage("解析 DOM 结构树...", 500),
        ScanMessage("构建组件层级关系...", 400),
        ScanMessage("生成交互热区映射...", 300),
        ScanMessage("验证识别置信度...", 200)
    )

    /**
     * 识别完成后的统计信息
     */
    fun generateRecognitionStats(hotspotCount: Int): RecognitionStats {
        return RecognitionStats(
            totalComponents = hotspotCount,
            avgConfidence = 94.0f + Random.nextFloat() * 5f,  // 94% - 99%
            processingTime = 2.1f + Random.nextFloat() * 0.8f,  // 2.1s - 2.9s
            modelVersion = "R-CNN v3.2.1",
            resolution = "1024 × 1024"
        )
    }

    // ============ 辅助函数 ============

    /**
     * 生成看起来真实的置信度（避免100%）
     */
    private fun generateConfidence(): Float {
        return 91.0f + Random.nextFloat() * 8.5f  // 91.0% - 99.5%
    }

    /**
     * 获取组件类型对应的颜色
     */
    fun getColorForType(type: ComponentType): Color {
        return when (type) {
            ComponentType.BUTTON -> Color(0xFF4CAF50)  // 绿色
            ComponentType.INPUT -> Color(0xFF2196F3)   // 蓝色
            ComponentType.TEXT -> Color(0xFFFF9800)    // 橙色
            ComponentType.IMAGE -> Color(0xFF9C27B0)   // 紫色
            ComponentType.LIST -> Color(0xFF00BCD4)    // 青色
            ComponentType.CARD -> Color(0xFFE91E63)    // 粉色
        }
    }
}

// ============ 数据类定义 ============

/**
 * 热区数据
 * @param rect 相对坐标 (0.0f - 1.0f)
 * @param type 组件类型
 * @param targetScreenId 点击后跳转的页面ID（null表示不跳转）
 */
data class Hotspot(
    val id: String,
    val rect: Rect,
    val type: ComponentType,
    val label: String,
    val confidence: Float,
    val targetScreenId: String? = null
)

/**
 * 演示页面数据
 */
data class DemoPage(
    val id: String,
    val name: String,
    val hotspots: List<Hotspot>
)

/**
 * 扫描过程消息
 */
data class ScanMessage(
    val text: String,
    val durationMs: Int
)

/**
 * 识别统计结果
 */
data class RecognitionStats(
    val totalComponents: Int,
    val avgConfidence: Float,
    val processingTime: Float,
    val modelVersion: String,
    val resolution: String
)
