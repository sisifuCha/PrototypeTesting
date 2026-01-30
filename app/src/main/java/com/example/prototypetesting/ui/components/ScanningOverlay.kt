package com.example.prototypetesting.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prototypetesting.data.DemoScenarioData
import com.example.prototypetesting.data.ScanMessage
import com.example.prototypetesting.ui.theme.*
import kotlinx.coroutines.delay

/**
 * AI 扫描动画覆盖层
 *
 * 显示效果：
 * 1. 从上到下的绿色扫描线
 * 2. 滚动显示的技术文案
 * 3. 进度条和完成百分比
 * 4. 科技感边框动画
 */
@Composable
fun ScanningOverlay(
    isScanning: Boolean,
    onScanComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMessageIndex by remember { mutableStateOf(0) }
    var overallProgress by remember { mutableFloatStateOf(0f) }
    var isCompleted by remember { mutableStateOf(false) }

    val messages = DemoScenarioData.scanningMessages

    // 扫描线动画
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    // 边框发光动画
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // 消息滚动和进度控制
    LaunchedEffect(isScanning) {
        if (isScanning) {
            currentMessageIndex = 0
            overallProgress = 0f
            isCompleted = false

            val totalDuration = messages.sumOf { it.durationMs }
            var elapsed = 0

            for ((index, message) in messages.withIndex()) {
                currentMessageIndex = index
                delay(message.durationMs.toLong())
                elapsed += message.durationMs
                overallProgress = elapsed.toFloat() / totalDuration
            }

            // 扫描完成
            overallProgress = 1f
            isCompleted = true
            delay(500)
            onScanComplete()
        }
    }

    AnimatedVisibility(
        visible = isScanning,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
        ) {
            // 科技感网格背景
            TechGridBackground()

            // 扫描线
            ScanLine(position = scanLinePosition)

            // 四角科技边框
            TechCorners(glowAlpha = glowAlpha)

            // 中央信息面板
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 旋转的 AI 图标
                RotatingAIIcon()

                Spacer(modifier = Modifier.height(24.dp))

                // 主标题
                Text(
                    text = if (isCompleted) "识别完成" else "AI 智能识别中",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF88)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 当前处理消息
                Card(
                    modifier = Modifier.widthIn(max = 320.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A2332)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCompleted) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF00FF88),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = if (isCompleted) {
                                "分析完成，正在生成结果..."
                            } else {
                                messages.getOrNull(currentMessageIndex)?.text ?: ""
                            },
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00FF88)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 进度条
                Column(
                    modifier = Modifier.width(280.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF00FF88),
                        trackColor = Color(0xFF2A3A4A),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${(overallProgress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00FF88)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 技术参数显示
                TechParametersDisplay()
            }
        }
    }
}

/**
 * 科技感网格背景
 */
@Composable
private fun TechGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 40.dp.toPx()
        val gridColor = Color(0xFF00FF88).copy(alpha = 0.05f)

        // 绘制垂直线
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += gridSize
        }

        // 绘制水平线
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += gridSize
        }
    }
}

/**
 * 扫描线
 */
@Composable
private fun ScanLine(position: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val y = size.height * position

        // 主扫描线
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF00FF88),
                    Color(0xFF00FF88),
                    Color.Transparent
                )
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 3.dp.toPx()
        )

        // 扫描线发光效果
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF00FF88).copy(alpha = 0.3f),
                    Color(0xFF00FF88).copy(alpha = 0.1f),
                    Color.Transparent
                ),
                startY = y - 50.dp.toPx(),
                endY = y + 20.dp.toPx()
            ),
            topLeft = Offset(0f, y - 50.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(size.width, 70.dp.toPx())
        )
    }
}

/**
 * 四角科技边框
 */
@Composable
private fun TechCorners(glowAlpha: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cornerLength = 60.dp.toPx()
        val strokeWidth = 3.dp.toPx()
        val padding = 20.dp.toPx()
        val color = Color(0xFF00FF88).copy(alpha = glowAlpha)

        // 左上角
        drawLine(color, Offset(padding, padding), Offset(padding + cornerLength, padding), strokeWidth)
        drawLine(color, Offset(padding, padding), Offset(padding, padding + cornerLength), strokeWidth)

        // 右上角
        drawLine(color, Offset(size.width - padding, padding), Offset(size.width - padding - cornerLength, padding), strokeWidth)
        drawLine(color, Offset(size.width - padding, padding), Offset(size.width - padding, padding + cornerLength), strokeWidth)

        // 左下角
        drawLine(color, Offset(padding, size.height - padding), Offset(padding + cornerLength, size.height - padding), strokeWidth)
        drawLine(color, Offset(padding, size.height - padding), Offset(padding, size.height - padding - cornerLength), strokeWidth)

        // 右下角
        drawLine(color, Offset(size.width - padding, size.height - padding), Offset(size.width - padding - cornerLength, size.height - padding), strokeWidth)
        drawLine(color, Offset(size.width - padding, size.height - padding), Offset(size.width - padding, size.height - padding - cornerLength), strokeWidth)
    }
}

/**
 * 旋转的 AI 图标
 */
@Composable
private fun RotatingAIIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "iconRotation"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer { rotationZ = rotation },
        contentAlignment = Alignment.Center
    ) {
        // 外圈
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF00FF88).copy(alpha = 0.3f),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // 内部图标
        Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = Color(0xFF00FF88),
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer { rotationZ = -rotation }  // 反向旋转保持图标正向
        )
    }
}

/**
 * 技术参数显示
 */
@Composable
private fun TechParametersDisplay() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TechParam(icon = Icons.Default.Memory, label = "GPU", value = "CUDA 12.1")
        TechParam(icon = Icons.Default.Analytics, label = "Model", value = "R-CNN v3.2")
        TechParam(icon = Icons.Default.Psychology, label = "Precision", value = "FP16")
    }
}

@Composable
private fun TechParam(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF00FF88).copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF00FF88).copy(alpha = 0.5f)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF00FF88).copy(alpha = 0.8f)
        )
    }
}

/**
 * 识别完成结果弹窗
 */
@Composable
fun RecognitionResultDialog(
    componentCount: Int,
    avgConfidence: Float,
    processingTime: Float,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2332),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI 识别完成",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                ResultRow("识别组件", "$componentCount 个")
                ResultRow("平均置信度", "${String.format("%.1f", avgConfidence)}%")
                ResultRow("处理时间", "${String.format("%.2f", processingTime)}s")
                ResultRow("模型版本", "R-CNN v3.2.1")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "已自动生成交互热区，点击确认进入编辑模式",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF88)
                )
            ) {
                Text("确认", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Color.White.copy(alpha = 0.7f))
            }
        }
    )
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00FF88)
        )
    }
}
