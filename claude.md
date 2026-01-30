# UX-Insight 多模态交互评测助手

## 项目概述

这是一个基于 Android Jetpack Compose 的智能 UI 测试助手前端原型，旨在解决传统 UI 设计中原型快速验证困难、可用性测试成本高、覆盖面窄的问题。项目当前处于 Demo 演示阶段，不连接后端。

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose (声明式 UI)
- **导航**: Navigation Compose 2.7.6
- **图片加载**: Coil Compose 2.5.0
- **最低 SDK**: Android 7.0 (API 24)
- **编译 SDK**: 34
- **JVM**: Java 11

## 项目结构

```
app/src/main/java/com/example/prototypetesting/
├── MainActivity.kt                 # 应用入口
├── data/
│   ├── Models.kt                   # 数据模型定义
│   └── SampleData.kt               # 示例数据
├── navigation/
│   ├── Screen.kt                   # 路由定义
│   └── NavGraph.kt                 # 导航图配置
└── ui/
    ├── screens/                    # 8 个页面
    │   ├── HomeScreen.kt           # 首页
    │   ├── UploadScreen.kt         # 上传原型
    │   ├── ProjectDetailScreen.kt  # 项目详情（AI识别结果）
    │   ├── TestRunnerScreen.kt     # 测试执行
    │   ├── TestManagementScreen.kt # 测试管理
    │   ├── AgentTestScreen.kt      # Agent测试（虚拟用户）
    │   ├── TestReportScreen.kt     # 测试报告
    │   └── ProfileScreen.kt        # 个人中心
    ├── components/
    │   ├── BottomNavigationBar.kt  # 底部导航栏
    │   └── ShareTestDialog.kt      # 分享对话框
    └── theme/
        ├── Color.kt                # 颜色定义
        ├── Theme.kt                # 主题配置
        └── Type.kt                 # 排版配置
```

## 路由配置

| 路由 | 页面 | 参数 |
|------|------|------|
| `home` | 首页 | 无 |
| `test_management` | 测试管理 | 无 |
| `agent_test` | Agent测试 | 无 |
| `profile` | 个人中心 | 无 |
| `upload` | 上传原型 | 无 |
| `project_detail/{projectName}/{imageUris}` | 项目详情 | projectName, imageUris |
| `test_runner/{projectName}/{imageUris}/{testType}` | 测试执行 | projectName, imageUris, testType |
| `test_report/{projectName}` | 测试报告 | projectName |

## 三大核心模块实现状态

### 模块一：低保真原型智能交互化

**功能目标**: 识别手绘纸质原型，自动补全跳转逻辑，实现"即画即测"

**实现状态**: ⭐⭐⭐ (中等 - UI 框架完成)

| 功能 | 状态 | 位置 |
|------|------|------|
| 多种上传方式（拍照/相册/扫描/文件） | ✅ 已实现 | `UploadScreen.kt` |
| 图片管理和预览 | ✅ 已实现 | `UploadScreen.kt` |
| AI识别界面（模拟） | ✅ 已实现 | `ProjectDetailScreen.kt` |
| 6种组件类型识别展示 | ✅ 已实现 | `ProjectDetailScreen.kt` |
| 实际AI识别算法集成 | ⏳ 待实现 | - |
| 组件自动连接逻辑 | ⏳ 待实现 | - |
| 跳转关系自动补全 | ⏳ 待实现 | - |

**支持的组件类型**: INPUT(输入框), BUTTON(按钮), TEXT(文本), IMAGE(图片), LIST(列表), CARD(卡片)

### 模块二：多模态情感可用性分析

**功能目标**: 融合屏幕操作流、面部微表情、语音流，多维度量化评估可用性

**实现状态**: ⭐⭐ (低 - 仅框架)

| 功能 | 状态 | 位置 |
|------|------|------|
| 测试报告UI框架（4标签页） | ✅ 已实现 | `TestReportScreen.kt` |
| 操作时间线记录展示 | ✅ 已实现 | `TestReportScreen.kt` |
| 用户路径分析展示 | ✅ 已实现 | `TestReportScreen.kt` |
| 热力图展示区域 | ✅ 已实现 | `TestReportScreen.kt` |
| 面部表情识别集成 | ⏳ 待实现 | - |
| 语音流处理分析 | ⏳ 待实现 | - |
| 屏幕操作流录制 | ⏳ 待实现 | - |
| 多模态数据融合算法 | ⏳ 待实现 | - |

**测试报告四个标签页**: 概览、热力图、时间线、洞察

### 模块三：虚拟用户仿真验收

**功能目标**: 模拟不同社会属性人群（老年人、学生等）进行自动化可用性验收

**实现状态**: ⭐⭐⭐ (中等 - UI 和模拟逻辑完成)

| 功能 | 状态 | 位置 |
|------|------|------|
| 4个预设用户画像 | ✅ 已实现 | `AgentTestScreen.kt` |
| Agent测试执行界面 | ✅ 已实现 | `TestRunnerScreen.kt` |
| Agent思考过程显示（模拟） | ✅ 已实现 | `TestRunnerScreen.kt` |
| 测试流程自动化模拟 | ✅ 已实现 | `TestRunnerScreen.kt` |
| 实际AI Agent实现 | ⏳ 待实现 | - |
| 自定义用户画像创建 | ⏳ 待实现 | - |
| 认知约束决策引擎 | ⏳ 待实现 | - |

**预设用户画像**:
- 科技新手 (55-65岁, 退休教师, 初级)
- 年轻白领 (25-35岁, 产品经理, 高级)
- 大学生 (18-24岁, 学生, 中级)
- 家庭主妇 (35-45岁, 全职妈妈, 初级)

## 数据模型

```kotlin
// 项目
data class Project(id, name, thumbnail, status, createdDate, pageCount)
enum class ProjectStatus { ONGOING, COMPLETED }

// 测试记录
data class TestRecord(id, projectName, testType, testerName, status, createdDate)
enum class TestType { USER_TEST, AGENT_TEST }
enum class TestStatus { PENDING, IN_PROGRESS, COMPLETED }

// 用户画像
data class UserPersona(id, name, ageRange, level, occupation, description, icon)
enum class PersonaLevel { BEGINNER, INTERMEDIATE, ADVANCED }

// UI组件
data class UIComponent(id, type, label, description)
enum class ComponentType { INPUT, BUTTON, TEXT, IMAGE, LIST, CARD }

// 用户档案
data class UserProfile(name, email, accountType, projectCount, testCount, agentCount, reportCount)
```

## 颜色主题

```kotlin
// 主色调
PrimaryBlue = #4A90E2
PrimaryBlueLight = #6BA3E8
PrimaryBlueDark = #3A7BC8

// 功能色
IconBlue = #2196F3     // 图标
IconCyan = #00BCD4     // 图标
IconOrange = #FF9800   // 图标
IconGreen = #4CAF50    // 图标

// 状态色
StatusGreen = #4CAF50  // 完成
StatusOrange = #FF9800 // 进行中
StatusRed = #F44336    // 错误
```

## 常用开发命令

```bash
# 构建项目
./gradlew build

# 安装到设备
./gradlew installDebug

# 运行测试
./gradlew test

# 清理构建
./gradlew clean
```

## 待开发功能清单

### 高优先级
1. 后端 API 集成 (建议使用 Retrofit)
2. 真实 AI 原型识别模块 (基于改进 R-CNN)
3. 面部表情识别 SDK 集成
4. 语音流处理模块
5. Agent 决策引擎实现

### 中优先级
6. 依赖注入框架 (Hilt)
7. 本地数据库 (Room)
8. 用户认证系统
9. 实时数据同步

### 低优先级
10. 深色模式完整适配
11. 国际化支持
12. 离线功能

## 开发注意事项

1. **Demo 模式**: 当前所有数据均为模拟数据，位于 `SampleData.kt`
2. **图片处理**: 使用 Coil 加载图片，上传的图片以 URI 字符串形式在路由间传递
3. **状态管理**: 使用 Compose 的 `remember` 和 `mutableStateOf` 进行状态管理
4. **导航**: 底部导航栏有 4 个主入口，其他页面通过参数传递进入
5. **主题**: 统一使用蓝色系主题，颜色定义在 `Color.kt`

## 相关文件快速索引

| 需求 | 文件 |
|------|------|
| 添加新页面 | 在 `screens/` 创建，并在 `NavGraph.kt` 和 `Screen.kt` 注册 |
| 修改底部导航 | `components/BottomNavigationBar.kt` |
| 添加数据模型 | `data/Models.kt` |
| 添加示例数据 | `data/SampleData.kt` |
| 修改主题颜色 | `ui/theme/Color.kt` |
| 修改全局样式 | `ui/theme/Theme.kt` |
