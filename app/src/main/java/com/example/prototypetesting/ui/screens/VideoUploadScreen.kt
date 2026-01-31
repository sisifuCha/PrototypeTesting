package com.example.prototypetesting.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.ui.theme.BackgroundBlue
import com.example.prototypetesting.ui.theme.PrimaryBlue
import com.example.prototypetesting.ui.theme.TextPrimary
import com.example.prototypetesting.ui.theme.TextSecondary
import com.example.prototypetesting.ui.theme.TextWhite
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoUploadScreen(navController: NavController) {
    var faceVideoUri by remember { mutableStateOf<Uri?>(null) }
    var screenVideoUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var messageIndex by remember { mutableStateOf(0) }
    var progressPercent by remember { mutableStateOf(0) }
    var showCursor by remember { mutableStateOf(true) }
    val messageAlpha = remember { Animatable(1f) }
    val progressAnim = remember { Animatable(0f) }

    val analysisMessages = listOf(
        "检测到用户正向情感波动、监测到视线搜索行为。",
        "判定为“初次识别困惑”，正在计算犹豫时长...",
        "用户成功通过登录关点，情绪效价转正。",
        "预测用户意图为“查看产品详情”。",
        "检测到无效点击 (Dead Click)。",
        "捕捉到负面语音流：“点不动”。满意度骤降。",
        "提示：正在融合面部/语音/操作数据，生成多模态可用性报告..."
    )

    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            messageIndex = 0
            progressPercent = 0
            progressAnim.snapTo(0f)
            val stepDelayMs = 700L
            val totalDurationMs = stepDelayMs * analysisMessages.size + 600L
            coroutineScope {
                launch {
                    progressAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = totalDurationMs.toInt(),
                            easing = LinearEasing
                        )
                    )
                }
                launch {
                    analysisMessages.indices.forEach { index ->
                        messageAlpha.snapTo(0f)
                        messageIndex = index
                        messageAlpha.animateTo(1f, animationSpec = tween(280))
                        delay(stepDelayMs - 280)
                    }
                }
            }
            delay(600L)
            isAnalyzing = false
            navController.navigate("ai_report")
        }
    }

    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            while (isAnalyzing) {
                showCursor = !showCursor
                delay(350L)
            }
        } else {
            showCursor = true
        }
    }

    val faceVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> faceVideoUri = uri }
    )
    val screenVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> screenVideoUri = uri }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = "发起测试", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TextWhite,
                    titleContentColor = TextPrimary
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = TextWhite),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "请上传两段视频用于测试分析",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "建议使用清晰的正脸与完整操作录屏",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                VideoUploadCard(
                    title = "上传人脸视频",
                    hint = "建议分辨率 1080p，格式 MP4",
                    selectedUri = faceVideoUri,
                    onClick = { faceVideoPicker.launch(arrayOf("video/*")) }
                )

                VideoUploadCard(
                    title = "上传操作录屏",
                    hint = "建议分辨率 1080p，格式 MP4",
                    selectedUri = screenVideoUri,
                    onClick = { screenVideoPicker.launch(arrayOf("video/*")) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(text = "取消")
                    }
                    Button(
                        onClick = { isAnalyzing = true },
                        enabled = faceVideoUri != null && screenVideoUri != null && !isAnalyzing,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(text = "AI分析")
                    }
                }
            }
        }

        if (isAnalyzing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TextWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
                        Text(
                            text = "${(progressAnim.value * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                        LinearProgressIndicator(
                            progress = progressAnim.value,
                            color = PrimaryBlue,
                            trackColor = PrimaryBlue.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "AI 正在分析中...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = buildString {
                                append(analysisMessages.getOrElse(messageIndex) { "" })
                                if (showCursor) append(" |")
                            },
                            fontSize = 13.sp,
                            color = TextSecondary.copy(alpha = messageAlpha.value)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoUploadCard(
    title: String,
    hint: String,
    selectedUri: Uri?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .border(1.dp, Color(0xFFE6ECF5), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = TextWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PrimaryBlue.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri == null) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "视频封面",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (selectedUri == null) "点击选择视频" else "已选择视频",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                if (selectedUri != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedUri.lastPathSegment ?: "video.mp4",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = hint,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
