package com.example.prototypetesting.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TestManagement : Screen("test_management")
    object AgentTest : Screen("agent_test")
    object AgentSimulation : Screen("agent_simulation")
    object Profile : Screen("profile")
    object Upload : Screen("upload")
    object ProjectDetail : Screen("project_detail")
    object TestRunner : Screen("test_runner")
    object TestReport : Screen("test_report")
    object PrototypePreview : Screen("prototype_preview")
    object VideoUpload : Screen("video_upload")
    object AiReport : Screen("ai_report")
}
