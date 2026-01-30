package com.example.prototypetesting.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.data.DemoScenarioData
import com.example.prototypetesting.data.Hotspot
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.components.InteractivePrototype
import com.example.prototypetesting.ui.theme.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.absoluteValue

/**
 * 原型预览页面 - 支持丝滑的上下翻页效果
 *
 * 使用 VerticalPager 实现：
 * 1. 上下滑动切换页面
 * 2. 弹性过渡动画
 * 3. 页面缩放效果
 * 4. 热区点击跳转
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrototypePreviewScreen(
    navController: NavController,
    projectName: String,
    imageUris: List<String>,
    initialPageIndex: Int = 0
) {
    val scope = rememberCoroutineScope()

    // Pager 状态
    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { imageUris.size }
    )

    var showHotspots by remember { mutableStateOf(true) }
    var interactionCount by remember { mutableIntStateOf(0) }
    var navigationHistory by remember { mutableStateOf(listOf(initialPageIndex)) }

    // 记录导航历史
    LaunchedEffect(pagerState.currentPage) {
        if (navigationHistory.lastOrNull() != pagerState.currentPage) {
            navigationHistory = navigationHistory + pagerState.currentPage
        }
    }

    // 处理热区点击跳转
    fun handleHotspotClick(hotspot: Hotspot) {
        interactionCount++
        if (hotspot.targetScreenId != null) {
            val targetIndex = DemoScenarioData.getPageIndexById(hotspot.targetScreenId)
            if (targetIndex in imageUris.indices && targetIndex != pagerState.currentPage) {
                scope.launch {
                    pagerState.animateScrollToPage(
                        page = targetIndex,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }
    }

    // 返回处理
    fun handleBack() {
        if (navigationHistory.size > 1) {
            val previousPage = navigationHistory.dropLast(1).last()
            navigationHistory = navigationHistory.dropLast(1)
            scope.launch {
                pagerState.animateScrollToPage(previousPage)
            }
        } else {
            navController.popBackStack()
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
                    currentPage = pagerState.currentPage + 1,
                    totalPages = imageUris.size,
                    interactionCount = interactionCount,
                    showHotspots = showHotspots
                )

                // 垂直翻页区域
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 40.dp),
                        pageSpacing = 20.dp,
                        beyondViewportPageCount = 1
                    ) { pageIndex ->
                        // 计算页面的偏移量，用于动画效果
                        val pageOffset = (
                            (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                        ).absoluteValue

                        PhoneFrameCard(
                            imageUri = imageUris[pageIndex],
                            pageIndex = pageIndex,
                            hotspots = DemoScenarioData.getHotspotsForPage(pageIndex),
                            showHotspots = showHotspots,
                            pageOffset = pageOffset,
                            onHotspotClick = { hotspot -> handleHotspotClick(hotspot) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                        )
                    }

                    // 右侧页面指示器
                    PageIndicator(
                        currentPage = pagerState.currentPage,
                        totalPages = imageUris.size,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    )
                }

                // 底部导航提示
                BottomNavigationHint(
                    currentPage = pagerState.currentPage,
                    totalPages = imageUris.size,
                    onPageClick = { targetPage ->
                        scope.launch {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }
                )
            }

            // 浮动操作提示
            SwipeHint(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp, 100.dp)
            )
        }
    }
}

/**
 * 手机框架卡片 - 带动画效果
 */
@Composable
private fun PhoneFrameCard(
    imageUri: String,
    pageIndex: Int,
    hotspots: List<Hotspot>,
    showHotspots: Boolean,
    pageOffset: Float,
    onHotspotClick: (Hotspot) -> Unit,
    modifier: Modifier = Modifier
) {
    // 根据偏移量计算缩放和透明度
    val scale = lerp(
        start = 0.85f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )

    val alpha = lerp(
        start = 0.5f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .fillMaxHeight(0.85f)
            .aspectRatio(0.48f)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF2A2A2A))
            .border(4.dp, Color(0xFF3A3A3A), RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.Center
    ) {
        // 手机内屏
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            // 刘海
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
                    .padding(top = 36.dp, bottom = 24.dp, start = 4.dp, end = 4.dp)
            ) {
                InteractivePrototype(
                    imageUri = imageUri,
                    hotspots = hotspots,
                    showHotspots = showHotspots,
                    onHotspotClick = onHotspotClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 页面标签
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 40.dp)
                    .background(Color(0xFF00FF88), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = DemoScenarioData.demoPages.getOrNull(pageIndex)?.name ?: "页面${pageIndex + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Home 指示器
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .width(100.dp)
                    .height(4.dp)
                    .background(Color(0xFF333333), RoundedCornerShape(2.dp))
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

        // 滑动提示
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.SwipeVertical,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "上下滑动翻页",
                fontSize = 13.sp,
                color = Color(0xFFFF9800)
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
    }
}

/**
 * 右侧页面指示器
 */
@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 12.dp else 8.dp)
                    .background(
                        color = if (isSelected) Color(0xFF00FF88) else Color(0xFF555555),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * 底部导航提示
 */
@Composable
private fun BottomNavigationHint(
    currentPage: Int,
    totalPages: Int,
    onPageClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A))
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            val pageName = DemoScenarioData.demoPages.getOrNull(index)?.name ?: "页面${index + 1}"

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) Color(0xFF00FF88).copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) Color(0xFF00FF88) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onPageClick(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = pageName,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF00FF88) else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * 滑动提示动画
 */
@Composable
private fun SwipeHint(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(true) }

    // 5秒后隐藏提示
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000)
        isVisible = false
    }

    // 上下浮动动画
    val infiniteTransition = rememberInfiniteTransition(label = "swipe")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Card(
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
                    imageVector = Icons.Default.SwipeVertical,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer { translationY = offsetY }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "上下滑动",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "切换页面",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
