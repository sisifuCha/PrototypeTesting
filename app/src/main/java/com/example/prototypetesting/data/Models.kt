package com.example.prototypetesting.data

import androidx.compose.ui.graphics.Color

data class Project(
    val id: String,
    val name: String,
    val thumbnail: String? = null,
    val status: ProjectStatus,
    val createdDate: String,
    val pageCount: Int = 0
)

enum class ProjectStatus {
    ONGOING,
    COMPLETED
}

data class TestRecord(
    val id: String,
    val projectName: String,
    val testType: TestType,
    val testerName: String,
    val status: TestStatus,
    val createdDate: String
)

enum class TestType {
    USER_TEST,
    AGENT_TEST
}

enum class TestStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class UserPersona(
    val id: String,
    val name: String,
    val ageRange: String,
    val level: PersonaLevel,
    val occupation: String,
    val description: String,
    val icon: String
)

enum class PersonaLevel(val displayName: String, val color: Color) {
    BEGINNER("初级", Color(0xFFFF9800)),
    INTERMEDIATE("中级", Color(0xFF2196F3)),
    ADVANCED("高级", Color(0xFF4CAF50))
}

data class UserProfile(
    val name: String,
    val email: String,
    val accountType: String,
    val projectCount: Int,
    val testCount: Int,
    val agentCount: Int,
    val reportCount: Int
)

data class StatCard(
    val count: Int,
    val label: String
)

data class Screen(
    val id: String,
    val name: String,
    val imageUrl: String,
    val components: List<UIComponent>,
    val order: Int
)

data class UIComponent(
    val id: String,
    val type: ComponentType,
    val label: String,
    val description: String = ""
)

enum class ComponentType(val displayName: String, val icon: String) {
    INPUT("输入框", "T"),
    BUTTON("按钮", "□"),
    TEXT("文本", "T"),
    IMAGE("图片", "🖼"),
    LIST("列表", "≡"),
    CARD("卡片", "▭")
}
