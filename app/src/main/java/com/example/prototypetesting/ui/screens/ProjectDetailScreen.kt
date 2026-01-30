package com.example.prototypetesting.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.data.ComponentType
import com.example.prototypetesting.data.DemoScenarioData
import com.example.prototypetesting.data.Hotspot
import com.example.prototypetesting.data.RecognitionStats
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.components.*
import com.example.prototypetesting.ui.theme.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 项目详情页面
 *
 * 集成 AI 扫描识别流程：
 * 1. 首次进入触发扫描动画
 * 2. 扫描完成显示识别结果
 * 3. 支持预览交互原型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: NavController,
    projectName: String,
    imageUris: List<String>
) {
    var currentScreenIndex by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }

    // AI 扫描状态
    var isScanning by remember { mutableStateOf(false) }
    var hasScanned by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var recognitionStats by remember { mutableStateOf<RecognitionStats?>(null) }

    // 当前页面的热区
    val currentHotspots = remember(currentScreenIndex, hasScanned) {
        if (hasScanned) {
            DemoScenarioData.getHotspotsForPage(currentScreenIndex)
        } else {
            emptyList()
        }
    }

    // 选中的热区
    var selectedHotspotId by remember { mutableStateOf<String?>(null) }

    // 首次进入时自动触发扫描（如果有图片）
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (hasScanned) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(StatusGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "已识别 · 页面 ${currentScreenIndex + 1}/${imageUris.size}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                } else {
                                    Text(
                                        text = "页面 ${currentScreenIndex + 1}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        // 重新扫描按钮
                        if (hasScanned) {
                            IconButton(onClick = {
                                hasScanned = false
                                isScanning = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "重新扫描",
                                    tint = TextSecondary
                                )
                            }
                        }

                        // 预览按钮
                        IconButton(
                            onClick = {
                                if (hasScanned) {
                                    val encodedUris = URLEncoder.encode(
                                        imageUris.joinToString(","),
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    val encodedName = URLEncoder.encode(
                                        projectName,
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navController.navigate("prototype_preview/$encodedName/$encodedUris/$currentScreenIndex")
                                }
                            },
                            enabled = hasScanned
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "预览原型",
                                tint = if (hasScanned) PrimaryBlue else TextSecondary.copy(alpha = 0.5f)
                            )
                        }

                        // 测试按钮
                        IconButton(onClick = {
                            val encodedUris = URLEncoder.encode(
                                imageUris.joinToString(","),
                                StandardCharsets.UTF_8.toString()
                            )
                            val encodedName = URLEncoder.encode(
                                projectName,
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("${Screen.TestRunner.route}/$encodedName/$encodedUris/user")
                        }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "开始测试",
                                tint = PrimaryBlue
                            )
                        }

                        // 保存按钮
                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("保存", fontSize = 13.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TextWhite
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundBlue)
                    .padding(paddingValues)
            ) {
                // Tab 栏
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = TextWhite,
                    contentColor = PrimaryBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RemoveRedEye,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("查看", fontSize = 14.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("编辑", fontSize = 14.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("连接", fontSize = 14.sp)
                            }
                        }
                    )
                }

                // 主内容区域
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundBlue)
                ) {
                    // 左侧预览区
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .background(TextWhite)
                            .padding(16.dp)
                    ) {
                        Column {
                            // 图片预览（带热区）
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.6f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BackgroundBlue)
                                    .border(2.dp, PrimaryBlue, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUris.isNotEmpty() && currentScreenIndex < imageUris.size) {
                                    InteractivePrototype(
                                        imageUri = imageUris[currentScreenIndex],
                                        hotspots = currentHotspots,
                                        showHotspots = hasScanned,
                                        onHotspotClick = { hotspot ->
                                            selectedHotspotId = hotspot.id
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 页面缩略图
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(imageUris) { index, uri ->
                                    ScreenThumbnail(
                                        index = index,
                                        isSelected = index == currentScreenIndex,
                                        onClick = { currentScreenIndex = index },
                                        imageUri = uri
                                    )
                                }
                                item {
                                    AddScreenButton()
                                }
                            }
                        }
                    }

                    // 右侧组件列表
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                            .background(TextWhite)
                            .padding(16.dp)
                    ) {
                        if (hasScanned) {
                            // 已识别 - 显示组件列表
                            Column {
                                // 识别统计
                                recognitionStats?.let { stats ->
                                    RecognitionStatsCard(stats)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                // 组件类型图例
                                ComponentTypeLegend(
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 组件列表
                                RecognizedComponentsList(
                                    hotspots = currentHotspots,
                                    selectedHotspotId = selectedHotspotId,
                                    onHotspotSelect = { hotspot ->
                                        selectedHotspotId = hotspot.id
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 预览按钮
                                Button(
                                    onClick = {
                                        val encodedUris = URLEncoder.encode(
                                            imageUris.joinToString(","),
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        val encodedName = URLEncoder.encode(
                                            projectName,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("prototype_preview/$encodedName/$encodedUris/$currentScreenIndex")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00C853)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("预览交互原型", fontSize = 15.sp)
                                }
                            }
                        } else {
                            // 未识别 - 显示等待扫描提示
                            WaitingForScanContent(
                                onStartScan = {
                                    isScanning = true
                                }
                            )
                        }
                    }
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

        // 识别结果对话框
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
                    }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

/**
 * 识别统计卡片
 */
@Composable
private fun RecognitionStatsCard(stats: RecognitionStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI 识别结果",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "${stats.totalComponents}",
                    label = "组件数",
                    color = Color(0xFF00BCD4)
                )
                StatItem(
                    value = "${String.format("%.1f", stats.avgConfidence)}%",
                    label = "置信度",
                    color = Color(0xFF00FF88)
                )
                StatItem(
                    value = "${String.format("%.2f", stats.processingTime)}s",
                    label = "耗时",
                    color = Color(0xFFFF9800)
                )
                StatItem(
                    value = stats.modelVersion.substringAfter("v"),
                    label = "模型",
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

/**
 * 等待扫描内容
 */
@Composable
private fun WaitingForScanContent(
    onStartScan: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "等待 AI 识别",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "点击下方按钮开始识别原型组件",
            fontSize = 14.sp,
            color = TextSecondary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartScan,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("开始 AI 识别", fontSize = 15.sp)
        }
    }
}

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TextPrimary.copy(alpha = 0.4f))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "页面${index + 1}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = if (isSelected) PrimaryBlue else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "页面${index + 1}",
                    fontSize = 10.sp,
                    color = if (isSelected) PrimaryBlue else TextSecondary
                )
            }
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
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "添加页面",
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}
