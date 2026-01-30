package com.example.prototypetesting.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.data.DemoScenarioData
import com.example.prototypetesting.data.Hotspot
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.components.InteractivePrototype
import com.example.prototypetesting.ui.theme.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 原型预览页面
 *
 * "即画即测"的核心页面，用户可以：
 * 1. 查看识别出的热区
 * 2. 点击热区进行页面跳转
 * 3. 模拟真实 App 的交互流程
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrototypePreviewScreen(
    navController: NavController,
    projectName: String,
    imageUris: List<String>,
    initialPageIndex: Int = 0
) {
    var currentPageIndex by remember { mutableIntStateOf(initialPageIndex) }
    var showHotspots by remember { mutableStateOf(true) }
    var showPageNavigator by remember { mutableStateOf(true) }
    var interactionCount by remember { mutableIntStateOf(0) }
    var navigationHistory by remember { mutableStateOf(listOf(initialPageIndex)) }

    // 获取当前页面的热区
    val currentHotspots = remember(currentPageIndex) {
        DemoScenarioData.getHotspotsForPage(currentPageIndex)
    }

    // 页面跳转动画
    var isTransitioning by remember { mutableStateOf(false) }

    // 处理热区点击
    fun handleHotspotClick(hotspot: Hotspot) {
        interactionCount++

        if (hotspot.targetScreenId != null) {
            val targetIndex = DemoScenarioData.getPageIndexById(hotspot.targetScreenId)
            if (targetIndex in imageUris.indices && targetIndex != currentPageIndex) {
                isTransitioning = true
                navigationHistory = navigationHistory + targetIndex
                currentPageIndex = targetIndex
            }
        }
    }

    // 返回上一页
    fun handleBack() {
        if (navigationHistory.size > 1) {
            navigationHistory = navigationHistory.dropLast(1)
            currentPageIndex = navigationHistory.last()
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(isTransitioning) {
        if (isTransitioning) {
            kotlinx.coroutines.delay(300)
            isTransitioning = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "原型预览",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = projectName.ifEmpty { "未命名项目" },
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 显示/隐藏热区
                    IconButton(onClick = { showHotspots = !showHotspots }) {
                        Icon(
                            imageVector = if (showHotspots) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "热区显示",
                            tint = if (showHotspots) PrimaryBlue else TextSecondary
                        )
                    }
                    // 开始测试
                    IconButton(onClick = {
                        val encodedUris = URLEncoder.encode(imageUris.joinToString(","), StandardCharsets.UTF_8.toString())
                        val encodedName = URLEncoder.encode(projectName, StandardCharsets.UTF_8.toString())
                        navController.navigate("${Screen.TestRunner.route}/$encodedName/$encodedUris/user")
                    }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "开始测试",
                            tint = PrimaryBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TextWhite
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(paddingValues)
        ) {
            // 主预览区域
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 状态栏
                PreviewStatusBar(
                    currentPage = currentPageIndex + 1,
                    totalPages = imageUris.size,
                    interactionCount = interactionCount,
                    showHotspots = showHotspots
                )

                // 原型预览
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentPageIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                // 向前导航
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                // 向后导航
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        },
                        label = "pageTransition"
                    ) { pageIndex ->
                        if (pageIndex in imageUris.indices) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(0.5f)  // 手机比例
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White)
                                    .border(3.dp, Color(0xFF333333), RoundedCornerShape(24.dp))
                            ) {
                                // 模拟手机刘海
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 8.dp)
                                        .width(80.dp)
                                        .height(24.dp)
                                        .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                                )

                                // 原型内容
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 36.dp, bottom = 20.dp, start = 4.dp, end = 4.dp)
                                ) {
                                    InteractivePrototype(
                                        imageUri = imageUris[pageIndex],
                                        hotspots = DemoScenarioData.getHotspotsForPage(pageIndex),
                                        showHotspots = showHotspots,
                                        onHotspotClick = { hotspot ->
                                            handleHotspotClick(hotspot)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // 模拟 Home 指示器
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                        .width(100.dp)
                                        .height(4.dp)
                                        .background(Color(0xFF333333), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }

                    // 页面切换过渡效果
                    if (isTransitioning) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF00FF88),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                // 页面导航器
                AnimatedVisibility(
                    visible = showPageNavigator && imageUris.size > 1,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    PageNavigator(
                        imageUris = imageUris,
                        currentIndex = currentPageIndex,
                        navigationHistory = navigationHistory,
                        onPageSelect = { index ->
                            if (index != currentPageIndex) {
                                navigationHistory = navigationHistory + index
                                currentPageIndex = index
                            }
                        }
                    )
                }
            }

            // 浮动提示
            FloatingHint(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp, 100.dp)
            )
        }
    }
}

/**
 * 预览状态栏
 */
@Composable
private fun PreviewStatusBar(
    currentPage: Int,
    totalPages: Int,
    interactionCount: Int,
    showHotspots: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 页面指示
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = Color(0xFF00FF88),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "页面 $currentPage / $totalPages",
                fontSize = 13.sp,
                color = Color.White
            )
        }

        // 交互计数
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = Color(0xFF00BCD4),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "交互: $interactionCount",
                fontSize = 13.sp,
                color = Color.White
            )
        }

        // 热区状态
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (showHotspots) Color(0xFF00FF88) else Color(0xFF666666),
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (showHotspots) "热区显示" else "热区隐藏",
                fontSize = 13.sp,
                color = if (showHotspots) Color(0xFF00FF88) else Color(0xFF666666)
            )
        }
    }
}

/**
 * 页面导航器
 */
@Composable
private fun PageNavigator(
    imageUris: List<String>,
    currentIndex: Int,
    navigationHistory: List<Int>,
    onPageSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A))
            .padding(16.dp)
    ) {
        // 导航历史
        if (navigationHistory.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "导航路径: ",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                navigationHistory.forEachIndexed { index, pageIndex ->
                    Text(
                        text = DemoScenarioData.demoPages.getOrNull(pageIndex)?.name ?: "页面${pageIndex + 1}",
                        fontSize = 11.sp,
                        color = if (index == navigationHistory.lastIndex) Color(0xFF00FF88) else Color.White.copy(alpha = 0.8f)
                    )
                    if (index < navigationHistory.lastIndex) {
                        Text(
                            text = " → ",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 页面缩略图
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(imageUris) { index, uri ->
                PageThumbnail(
                    imageUri = uri,
                    pageIndex = index,
                    pageName = DemoScenarioData.demoPages.getOrNull(index)?.name ?: "页面${index + 1}",
                    isSelected = index == currentIndex,
                    isInHistory = index in navigationHistory,
                    onClick = { onPageSelect(index) }
                )
            }
        }
    }
}

/**
 * 页面缩略图
 */
@Composable
private fun PageThumbnail(
    imageUri: String,
    pageIndex: Int,
    pageName: String,
    isSelected: Boolean,
    isInHistory: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .aspectRatio(0.6f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF3A3A3A))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = when {
                        isSelected -> Color(0xFF00FF88)
                        isInHistory -> Color(0xFF00BCD4).copy(alpha = 0.5f)
                        else -> Color(0xFF555555)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
        ) {
            AsyncImage(
                model = Uri.parse(imageUri),
                contentDescription = pageName,
                modifier = Modifier.fillMaxSize()
            )

            // 选中标记
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF00FF88).copy(alpha = 0.1f))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = pageName,
            fontSize = 10.sp,
            color = if (isSelected) Color(0xFF00FF88) else Color.White.copy(alpha = 0.7f)
        )
    }
}

/**
 * 浮动提示
 */
@Composable
private fun FloatingHint(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000)
        isExpanded = false
    }

    AnimatedVisibility(
        visible = true,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A).copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(20.dp)
                )

                AnimatedVisibility(visible = isExpanded) {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "点击绿色按钮可跳转页面",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
