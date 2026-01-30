# 纸质原型测试 Android App

一个用于纸质低保真原型测试的Android应用，支持原型上传、用户测试、AI Agent测试和测试报告生成。

## 功能特性

### 1. 原型管理
- **多种上传方式**: 支持拍照、相册选择等多种方式上传纸质原型
- **AI自动识别**: 自动识别草图上的组件功能和页面跳转关系
- **手动编辑**: 支持人工修改和调整识别结果
- **项目管理**: 查看和管理所有原型项目

### 2. 用户测试
- **测试分发**: 将可交互原型发送给同事和用户进行测试
- **过程记录**: 记录测试过程中的点击时间、操作路径等数据
- **视频录制**: 录像记录测试者的状态和反应
- **测试报告**: 自动生成详细的测试报告

### 3. AI Agent测试
- **用户画像**: 预设多种用户画像（科技新手、年轻白领、大学生、家庭主妇等）
- **自定义画像**: 支持自定义用户群体特征
- **智能测试**: Agent模拟真实用户进行测试
- **思考过程**: 记录Agent的思考和决策过程
- **详细报告**: 生成包含思考过程和操作记录的测试报告

### 4. 测试管理
- **测试追踪**: 管理和追踪所有测试会话
- **状态筛选**: 按待测试、进行中、已完成等状态筛选
- **快速分享**: 快速分享测试项目给测试者
- **历史记录**: 查看所有测试历史记录

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM (准备中)
- **导航**: Navigation Compose
- **依赖注入**: 准备集成 Hilt/Koin
- **网络请求**: 准备集成 Retrofit
- **图片加载**: Coil

## 项目结构

```
app/src/main/java/com/example/prototypetesting/
├── data/                          # 数据层
│   ├── Models.kt                  # 数据模型
│   └── SampleData.kt             # 示例数据
├── navigation/                    # 导航
│   ├── Screen.kt                 # 路由定义
│   └── NavGraph.kt               # 导航图
├── ui/
│   ├── components/               # UI组件
│   │   └── BottomNavigationBar.kt
│   ├── screens/                  # 页面
│   │   ├── HomeScreen.kt         # 首页
│   │   ├── TestManagementScreen.kt  # 测试管理
│   │   ├── AgentTestScreen.kt    # AI Agent测试
│   │   └── ProfileScreen.kt      # 个人中心
│   └── theme/                    # 主题
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt               # 主Activity
```

## 主要页面

### 首页 (HomeScreen)
- 项目统计卡片（总数、进行中、已完成）
- 快速操作按钮（上传原型、用户测试、Agent测试、测试报告）
- 创建新项目入口
- 最近项目列表

### 测试管理 (TestManagementScreen)
- 搜索功能
- 状态筛选（全部、待测试、进行中、已完成）
- 快速分享测试
- 测试记录列表

### AI Agent测试 (AgentTestScreen)
- 用户画像卡片展示
- 自定义画像功能
- 选择测试项目
- 启动Agent测试

### 个人中心 (ProfileScreen)
- 用户信息展示
- 使用统计（项目、测试、Agent、报告数量）
- 升级提示
- 设置选项（通知、深色模式、语言、隐私安全等）

## 颜色主题

应用采用蓝色渐变主题，主要颜色包括：
- 主色调: `#4A90E2` (PrimaryBlue)
- 辅助色: `#5BA3F5` (SecondaryBlue)
- 背景色: `#F5F8FC` (BackgroundBlue)
- 功能色: 蓝色、青色、橙色、绿色

## 开发计划

### 已完成
- ✅ 基础UI框架
- ✅ 四个主要页面
- ✅ 底部导航
- ✅ 主题配置
- ✅ 示例数据

### 待开发
- ⏳ 后端API集成
- ⏳ 图片上传功能
- ⏳ AI识别集成
- ⏳ 测试录制功能
- ⏳ Agent测试引擎
- ⏳ 报告生成功能
- ⏳ 数据持久化
- ⏳ 用户认证

## 构建和运行

1. 克隆项目
2. 使用 Android Studio 打开项目
3. 同步 Gradle 依赖
4. 运行应用（最低支持 Android 7.0 / API 24）

## 依赖项

主要依赖包括：
- Jetpack Compose BOM
- Navigation Compose 2.7.6
- Material Icons Extended 1.5.4
- Coil 2.5.0
- Lifecycle ViewModel Compose 2.7.0

## 许可证

待定
