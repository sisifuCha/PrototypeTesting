package com.example.prototypetesting.data

object SampleData {
    val projects = listOf(
        Project(
            id = "1",
            name = "未命名项目",
            status = ProjectStatus.ONGOING,
            createdDate = "2026/1/28",
            pageCount = 0
        ),
        Project(
            id = "2",
            name = "电商App登录流程",
            status = ProjectStatus.ONGOING,
            createdDate = "2025/1/25",
            pageCount = 4
        ),
        Project(
            id = "3",
            name = "社交媒体首页",
            status = ProjectStatus.ONGOING,
            createdDate = "2025/1/20",
            pageCount = 3
        )
    )

    val testRecords = listOf(
        TestRecord(
            id = "1",
            projectName = "电商App登录流程",
            testType = TestType.USER_TEST,
            testerName = "李明",
            status = TestStatus.COMPLETED,
            createdDate = "2026/1/28"
        ),
        TestRecord(
            id = "2",
            projectName = "电商App登录流程",
            testType = TestType.AGENT_TEST,
            testerName = "年轻白领",
            status = TestStatus.COMPLETED,
            createdDate = "2026/1/27"
        ),
        TestRecord(
            id = "3",
            projectName = "电商App登录流程",
            testType = TestType.USER_TEST,
            testerName = "王强",
            status = TestStatus.PENDING,
            createdDate = "2026/1/26"
        )
    )

    val userPersonas = listOf(
        UserPersona(
            id = "1",
            name = "科技新手",
            ageRange = "55-65岁",
            level = PersonaLevel.BEGINNER,
            occupation = "退休教师",
            description = "对科技产品不太熟悉，需要清晰的引导",
            icon = "👴"
        ),
        UserPersona(
            id = "2",
            name = "年轻白领",
            ageRange = "25-35岁",
            level = PersonaLevel.ADVANCED,
            occupation = "产品经理",
            description = "追求效率，期待流畅的用户体验",
            icon = "👨"
        ),
        UserPersona(
            id = "3",
            name = "大学生",
            ageRange = "18-24岁",
            level = PersonaLevel.INTERMEDIATE,
            occupation = "学生",
            description = "乐于尝试新事物，对设计有自己的见解",
            icon = "👨‍🎓"
        ),
        UserPersona(
            id = "4",
            name = "家庭主妇",
            ageRange = "35-45岁",
            level = PersonaLevel.BEGINNER,
            occupation = "全职妈妈",
            description = "注重实用性，喜欢简单直接的操作",
            icon = "👩"
        )
    )

    val userProfile = UserProfile(
        name = "张三",
        email = "zhangsan@company.com",
        accountType = "企业版",
        projectCount = 8,
        testCount = 24,
        agentCount = 12,
        reportCount = 18
    )
}
