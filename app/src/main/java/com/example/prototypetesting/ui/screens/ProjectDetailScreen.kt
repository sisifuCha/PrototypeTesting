package com.example.prototypetesting.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.data.DemoScenarioData
import com.example.prototypetesting.data.Hotspot
import com.example.prototypetesting.data.RecognitionStats
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.components.*
import com.example.prototypetesting.ui.theme.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 项目详情页面 - 手机端优化版
 *
 * 布局：
 * 1. 图片全屏显示（带热区）
 * 2. 顶部浮动统计栏（可收起）
 * 3. 底部组件抽屉（可展开/收起）
 * 4. 底部页面缩略图导航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: NavController,
    projectName: String,
    imageUris: List<String>
) {
    var currentScreenIndex by remember { mutableIntStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }

    // AI 扫描状态
    var isScanning by remember { mutableStateOf(false) }
    var hasScanned by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var recognitionStats by remember { mutableStateOf<RecognitionStats?>(null) }

    // UI 状态
    var showStatsBar by remember { mutableStateOf(true) }
    var showComponentSheet by remember { mutableStateOf(false) }
    var selectedHotspotId by remember { mutableStateOf<String?>(null) }

    // 当前页面的热区
    val currentHotspots = remember(currentScreenIndex, hasScanned) {
        if (hasScanned) {
            DemoScenarioData.getHotspotsForPage(currentScreenIndex)
        } else {
            emptyList()
        }
    }

    // 首次进入时自动触发扫描
    LaunchedEffect(imageUris) {
        if (imageUris.isNotEmpty() && !hasScanned) {
            kotlinx.coroutines.delay(500)
            isScanning = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = projectName.ifEmpty { "未命名项目" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (hasScanned) {
                                Text(
                                    text = "页面 ${currentScreenIndex + 1}/${imageUris.size} · ${currentHotspots.size} 个组件",
                                    fontSize = 12.sp,
                                    color = StatusGreen
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        // 重新扫描
                        if (hasScanned) {
                            IconButton(onClick = {
                                hasScanned = false
                                isScanning = true
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "重新扫描", tint = TextSecondary)
                            }
                        }
                        // 预览原型
                        IconButton(
                            onClick = {
                                if (hasScanned) {
                                    val encodedUris = URLEncoder.encode(imageUris.joinToString(","), StandardCharsets.UTF_8.toString())
                                    val encodedName = URLEncoder.encode(projectName, StandardCharsets.UTF_8.toString())
                                    navController.navigate("prototype_preview/$encodedName/$encodedUris/$currentScreenIndex")
                                }
                            },
                            enabled = hasScanned
                        ) {
                            Icon(
                                Icons.Default.PlayCircle,
                                contentDescription = "预览",
                                tint = if (hasScanned) PrimaryBlue else TextSecondary.copy(alpha = 0.5f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = TextWhite)
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1A1A1A))
            ) {
                // 主图片区域
                Column(modifier = Modifier.fillMaxSize()) {
                    // 图片预览（占据主要空间）
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUris.isNotEmpty() && currentScreenIndex < imageUris.size) {
                            // 手机框架 + 图片
                            PhoneFrameWithImage(
                                imageUri = imageUris[currentScreenIndex],
                                hotspots = currentHotspots,
                                showHotspots = hasScanned,
                                selectedHotspotId = selectedHotspotId,
                                onHotspotClick = { hotspot ->
                                    selectedHotspotId = hotspot.id
                                    // 如果有跳转目标，执行跳转
                                    if (hotspot.targetScreenId != null) {
                                        val targetIndex = DemoScenarioData.getPageIndexById(hotspot.targetScreenId)
                                        if (targetIndex in imageUris.indices) {
                                            currentScreenIndex = targetIndex
                                        }
                                    }
                                }
                            )
                        } else {
                            // 空状态
                            EmptyImagePlaceholder()
                        }

                        // 浮动的识别统计（左上角）
                        if (hasScanned && showStatsBar) {
                            FloatingStatsBar(
                                stats = recognitionStats,
                                onClose = { showStatsBar = false },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            )
                        }

                        // 显示统计按钮（当隐藏时）
                        if (hasScanned && !showStatsBar) {
                            FloatingActionButton(
                                onClick = { showStatsBar = true },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .size(40.dp),
                                containerColor = Color(0xFF00FF88),
                                contentColor = Color.Black
                            ) {
                                Icon(Icons.Default.Analytics, contentDescription = "显示统计", modifier = Modifier.size(20.dp))
                            }
                        }

                        // 组件列表按钮（右下角）
                        if (hasScanned && currentHotspots.isNotEmpty()) {
                            FloatingActionButton(
                                onClick = { showComponentSheet = true },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp),
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            ) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color(0xFF00FF88)) {
                                            Text("${currentHotspots.size}", fontSize = 10.sp, color = Color.Black)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Widgets, contentDescription = "组件列表")
                                }
                            }
                        }
                    }

                    // 底部页面缩略图导航
                    PageThumbnailBar(
                        imageUris = imageUris,
                        currentIndex = currentScreenIndex,
                        onPageSelect = { currentScreenIndex = it },
                        onPreview = {
                            if (hasScanned) {
                                val encodedUris = URLEncoder.encode(imageUris.joinToString(","), StandardCharsets.UTF_8.toString())
                                val encodedName = URLEncoder.encode(projectName, StandardCharsets.UTF_8.toString())
                                navController.navigate("prototype_preview/$encodedName/$encodedUris/$currentScreenIndex")
                            }
                        }
                    )
                }

                // 未扫描时显示开始按钮
                if (!hasScanned && !isScanning && imageUris.isNotEmpty()) {
                    StartScanButton(
                        onStartScan = { isScanning = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // 扫描动画覆盖层
        ScanningOverlay(
            isScanning = isScanning,
            onScanComplete = {
                isScanning = false
                hasScanned = true
                val hotspots = DemoScenarioData.getHotspotsForPage(currentScreenIndex)
                recognitionStats = DemoScenarioData.generateRecognitionStats(hotspots.size)
                showResultDialog = true
            },
            modifier = Modifier.fillMaxSize()
        )

        // 组件列表底部抽屉
        if (showComponentSheet) {
            ComponentBottomSheet(
                hotspots = currentHotspots,
                selectedHotspotId = selectedHotspotId,
                onHotspotSelect = { hotspot ->
                    selectedHotspotId = hotspot.id
                },
                onHotspotNavigate = { hotspot ->
                    if (hotspot.targetScreenId != null) {
                        val targetIndex = DemoScenarioData.getPageIndexById(hotspot.targetScreenId)
                        if (targetIndex in imageUris.indices) {
                            currentScreenIndex = targetIndex
                            showComponentSheet = false
                        }
                    }
                },
                onDismiss = { showComponentSheet = false }
            )
        }

        // 识别完成对话框
        if (showResultDialog && recognitionStats != null) {
            RecognitionResultDialog(
                componentCount = recognitionStats!!.totalComponents,
                avgConfidence = recognitionStats!!.avgConfidence,
                processingTime = recognitionStats!!.processingTime,
                onDismiss = { showResultDialog = false },
                onConfirm = { showResultDialog = false }
            )
        }

        // 保存对话框
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("保存项目") },
                text = { Text("项目已保存成功！") },
                confirmButton = {
                    TextButton(onClick = {
                        showSaveDialog = false
                        navController.popBackStack()
                    }) { Text("确定") }
                }
            )
        }
    }
}

/**
 * 手机框架 + 图片显示
 */
@Composable
private fun PhoneFrameWithImage(
    imageUri: String,
    hotspots: List<Hotspot>,
    showHotspots: Boolean,
    selectedHotspotId: String?,
    onHotspotClick: (Hotspot) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF2A2A2A))
            .border(3.dp, Color(0xFF3A3A3A), RoundedCornerShape(24.dp))
            .padding(6.dp)
    ) {
        // 内屏
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
        ) {
            InteractivePrototype(
                imageUri = imageUri,
                hotspots = hotspots,
                showHotspots = showHotspots,
                onHotspotClick = onHotspotClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 浮动的识别统计栏
 */
@Composable
private fun FloatingStatsBar(
    stats: RecognitionStats?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE6000000))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI 图标
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = Color(0xFF00FF88),
                modifier = Modifier.size(20.dp)
            )

            // 统计数据
            if (stats != null) {
                StatChip(value = "${stats.totalComponents}", label = "组件", color = Color(0xFF00BCD4))
                StatChip(value = "${String.format("%.1f", stats.avgConfidence)}%", label = "置信", color = Color(0xFF00FF88))
                StatChip(value = "${String.format("%.1f", stats.processingTime)}s", label = "耗时", color = Color(0xFFFF9800))
            }

            // 关闭按钮
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun StatChip(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = color
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

/**
 * 底部页面缩略图导航栏
 */
@Composable
private fun PageThumbnailBar(
    imageUris: List<String>,
    currentIndex: Int,
    onPageSelect: (Int) -> Unit,
    onPreview: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 页面缩略图
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(imageUris) { index, uri ->
                MiniPageThumbnail(
                    imageUri = uri,
                    pageIndex = index,
                    isSelected = index == currentIndex,
                    onClick = { onPageSelect(index) }
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 预览按钮
        Button(
            onClick = onPreview,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88)),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text("预览", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
private fun MiniPageThumbnail(
    imageUri: String,
    pageIndex: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val pageName = DemoScenarioData.demoPages.getOrNull(pageIndex)?.name ?: "页面${pageIndex + 1}"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp, 70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF3A3A3A))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF00FF88) else Color(0xFF555555),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
        ) {
            AsyncImage(
                model = Uri.parse(imageUri),
                contentDescription = pageName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = pageName,
            fontSize = 9.sp,
            color = if (isSelected) Color(0xFF00FF88) else Color.White.copy(alpha = 0.6f)
        )
    }
}

/**
 * 开始扫描按钮
 */
@Composable
private fun StartScanButton(
    onStartScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE6000000))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = Color(0xFF00FF88),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("点击开始 AI 识别", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStartScan,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("开始识别", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 空图片占位
 */
@Composable
private fun EmptyImagePlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("暂无图片", color = Color.White.copy(alpha = 0.5f))
    }
}

/**
 * 组件列表底部抽屉
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComponentBottomSheet(
    hotspots: List<Hotspot>,
    selectedHotspotId: String?,
    onHotspotSelect: (Hotspot) -> Unit,
    onHotspotNavigate: (Hotspot) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFF555555), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "识别组件",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = Color(0xFF00FF88)) {
                            Text("${hotspots.size}", color = Color.Black)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭", tint = Color.White.copy(alpha = 0.6f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 组件类型图例
            ComponentLegendRow()

            Spacer(modifier = Modifier.height(8.dp))

            // 组件列表
            hotspots.forEach { hotspot ->
                ComponentListItem(
                    hotspot = hotspot,
                    isSelected = hotspot.id == selectedHotspotId,
                    onClick = { onHotspotSelect(hotspot) },
                    onNavigate = { onHotspotNavigate(hotspot) }
                )
            }
        }
    }
}

@Composable
private fun ComponentLegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF4CAF50), label = "按钮")
        LegendItem(color = Color(0xFF2196F3), label = "输入")
        LegendItem(color = Color(0xFFFF9800), label = "文本")
        LegendItem(color = Color(0xFF9C27B0), label = "图片")
        LegendItem(color = Color(0xFF00BCD4), label = "列表")
        LegendItem(color = Color(0xFFE91E63), label = "卡片")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun ComponentListItem(
    hotspot: Hotspot,
    isSelected: Boolean,
    onClick: () -> Unit,
    onNavigate: () -> Unit
) {
    val boxColor = DemoScenarioData.getColorForType(hotspot.type)
    val canNavigate = hotspot.targetScreenId != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) boxColor.copy(alpha = 0.15f) else Color(0xFF2A2A2A)
        ),
        border = if (isSelected) BorderStroke(2.dp, boxColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 类型图标
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(boxColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(hotspot.type.icon, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = hotspot.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    if (canNavigate) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(containerColor = Color(0xFF4CAF50)) {
                            Text("可跳转", fontSize = 9.sp, color = Color.White)
                        }
                    }
                }
                Text(
                    text = hotspot.type.displayName,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // 置信度
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.1f", hotspot.confidence)}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = boxColor
                )
                Text("置信度", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
            }

            // 跳转按钮
            if (canNavigate) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onNavigate,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "跳转",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// 保留原有的辅助组件
@Composable
fun ScreenThumbnail(
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    imageUri: String? = null
) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else BackgroundBlue)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryBlue else TextSecondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = Uri.parse(imageUri),
                contentDescription = "页面${index + 1}",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun AddScreenButton() {
    Box(
        modifier = Modifier
            .width(60.dp)
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundBlue)
            .border(1.dp, TextSecondary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = "添加页面", tint = TextSecondary, modifier = Modifier.size(24.dp))
    }
}
