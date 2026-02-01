package com.example.prototypetesting.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.prototypetesting.data.ComponentType
import com.example.prototypetesting.data.DemoScenarioData
import com.example.prototypetesting.data.Hotspot
import kotlinx.coroutines.delay

/**
 * 交互式原型组件
 *
 * 在图片上绘制可点击的热区框，支持：
 * 1. 动态显示识别框（带动画）
 * 2. 显示组件类型标签和置信度
 * 3. 点击热区触发页面跳转
 * 4. 涟漪点击效果
 */
@Composable
fun InteractivePrototype(
    imageUri: String,
    hotspots: List<Hotspot>,
    showHotspots: Boolean = true,
    onHotspotClick: (Hotspot) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var clickPosition by remember { mutableStateOf<Offset?>(null) }
    var showRipple by remember { mutableStateOf(false) }

    // 热区逐个显示动画
    var visibleHotspotCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(showHotspots, hotspots) {
        if (showHotspots) {
            visibleHotspotCount = 0
            for (i in hotspots.indices) {
                delay(150)  // 每个框间隔出现
                visibleHotspotCount = i + 1
            }
        }
    }

    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .onGloballyPositioned { coordinates ->
                containerSize = coordinates.size
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    clickPosition = offset
                    showRipple = true
                }
            }
    ) {
        // 背景图片 - 使用 SubcomposeAsyncImage 以便显示加载状态和错误状态
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(Uri.parse(imageUri))
                .crossfade(true)
                .build(),
            contentDescription = "原型图片",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF00FF88),
                        strokeWidth = 3.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFAFAFA)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "图片加载失败",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        )

        // 热区覆盖层
        if (showHotspots && containerSize.width > 0) {
            hotspots.take(visibleHotspotCount).forEachIndexed { index, hotspot ->
                HotspotBox(
                    hotspot = hotspot,
                    containerSize = containerSize,
                    onClick = { onHotspotClick(hotspot) },
                    animationDelay = index * 50
                )
            }
        }

        // 点击涟漪效果
        if (showRipple && clickPosition != null) {
            ClickRippleEffect(
                position = clickPosition!!,
                onComplete = { showRipple = false }
            )
        }
    }
}

/**
 * 单个热区框
 */
@Composable
private fun HotspotBox(
    hotspot: Hotspot,
    containerSize: IntSize,
    onClick: () -> Unit,
    animationDelay: Int
) {
    val density = LocalDensity.current
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    // 出场动画
    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    // 计算实际像素位置
    val left = (hotspot.rect.left * containerSize.width).toInt()
    val top = (hotspot.rect.top * containerSize.height).toInt()
    val width = ((hotspot.rect.right - hotspot.rect.left) * containerSize.width).toInt()
    val height = ((hotspot.rect.bottom - hotspot.rect.top) * containerSize.height).toInt()

    val boxColor = DemoScenarioData.getColorForType(hotspot.type)

    // 脉冲动画（可点击热区）
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (hotspot.targetScreenId != null) 0.6f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        modifier = Modifier
            .offset(
                x = with(density) { left.toDp() },
                y = with(density) { top.toDp() }
            )
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = with(density) { width.toDp() },
                    height = with(density) { height.toDp() }
                )
                .background(
                    boxColor.copy(alpha = if (isPressed) 0.4f else pulseAlpha * 0.5f),
                    RoundedCornerShape(4.dp)
                )
                .border(
                    width = 2.dp,
                    color = boxColor.copy(alpha = if (isPressed) 1f else 0.8f),
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    isPressed = true
                    onClick()
                }
        ) {
            // 标签
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = (-20).dp)
                    .background(boxColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = hotspot.type.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format("%.1f", hotspot.confidence)}%",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // 可点击指示器
            if (hotspot.targetScreenId != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .background(Color.White, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "可点击",
                        tint = boxColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * 点击涟漪效果
 */
@Composable
private fun ClickRippleEffect(
    position: Offset,
    onComplete: () -> Unit
) {
    var radius by remember { mutableFloatStateOf(0f) }
    var alpha by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(position) {
        // 展开动画
        animate(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) { value, _ ->
            radius = value
            alpha = 1f - (value / 100f)
        }
        onComplete()
    }

    val density = LocalDensity.current

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        drawCircle(
            color = Color(0xFF00FF88).copy(alpha = alpha * 0.5f),
            radius = radius,
            center = position
        )
        drawCircle(
            color = Color(0xFF00FF88).copy(alpha = alpha * 0.8f),
            radius = radius,
            center = position,
            style = Stroke(width = 3f)
        )
    }
}

/**
 * 组件识别结果列表
 */
@Composable
fun RecognizedComponentsList(
    hotspots: List<Hotspot>,
    selectedHotspotId: String?,
    onHotspotSelect: (Hotspot) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "识别组件 (${hotspots.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        hotspots.forEach { hotspot ->
            RecognizedComponentItem(
                hotspot = hotspot,
                isSelected = hotspot.id == selectedHotspotId,
                onClick = { onHotspotSelect(hotspot) }
            )
        }
    }
}

@Composable
private fun RecognizedComponentItem(
    hotspot: Hotspot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val boxColor = DemoScenarioData.getColorForType(hotspot.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                boxColor.copy(alpha = 0.15f)
            } else {
                Color(0xFFF5F8FC)
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, boxColor)
        } else null
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
                    .size(40.dp)
                    .background(boxColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = hotspot.type.icon,
                    fontSize = 18.sp,
                    color = boxColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotspot.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A1A)
                    )
                    if (hotspot.targetScreenId != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(
                            containerColor = Color(0xFF4CAF50)
                        ) {
                            Text(
                                text = "可跳转",
                                fontSize = 9.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = hotspot.type.displayName,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            // 置信度
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${String.format("%.1f", hotspot.confidence)}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = boxColor
                )
                Text(
                    text = "置信度",
                    fontSize = 10.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

/**
 * 组件类型图例
 */
@Composable
fun ComponentTypeLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F8FC)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "组件类型图例",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ComponentType.entries.forEach { type ->
                    LegendItem(
                        color = DemoScenarioData.getColorForType(type),
                        label = type.displayName
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF757575)
        )
    }
}
