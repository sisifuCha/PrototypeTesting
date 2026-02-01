package com.example.prototypetesting.ui.screens

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.prototypetesting.data.TimelineLoader
import com.example.prototypetesting.ui.components.TimelineView
import com.example.prototypetesting.ui.theme.BackgroundBlue
import com.example.prototypetesting.ui.theme.PrimaryBlue
import com.example.prototypetesting.ui.theme.TextPrimary
import com.example.prototypetesting.ui.theme.TextSecondary
import com.example.prototypetesting.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiReportScreen(navController: NavController) {
    val context = LocalContext.current
    val reportContent = buildReportContent()
    val scope = rememberCoroutineScope()
    val timelineData = remember { TimelineLoader.loadFromAssets(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEAF2FF),
                        Color(0xFFF7FAFF)
                    )
                )
            )
    ) {
        TopAppBar(
            title = { Text(text = "AI 分析报告", fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val result = saveReportToDownloads(
                                navController.context,
                                "PaperProto-AI-Report-0824.txt",
                                reportContent
                            )
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    navController.context,
                                    if (result) "已下载到下载目录" else "下载失败",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "下载报告",
                        tint = PrimaryBlue
                    )
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TextWhite),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        PrimaryBlue.copy(alpha = 0.12f),
                                        Color(0xFFEEF5FF)
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "AI 深度评测摘要",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                    Text(
                        text = "PaperProto-AI 深度评测报告",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "多模态可用性分析 · Session #0824",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .background(PrimaryBlue.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "AI Report",
                                fontSize = 11.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5FF), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "完成",
                                fontSize = 11.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 时间轴展示
            timelineData?.let { data ->
                TimelineView(timelineData = data)
            }

            ReportSection(
                title = "1. 有效性 (Effectiveness)",
                body = listOf(
                    "任务完成率：100%（成功达成确认目标）。",
                    "AI 洞察：用户虽然发生误触，但最终独立修正了路径，未发生任务中断。"
                ),
                accent = Color(0xFF4CAF50)
            )

            ReportSection(
                title = "2. 效率 (Efficiency)",
                body = listOf(
                    "犹豫时长：15.4s。主要断点发生在登录页（寻找按钮）。",
                    "操作量：12 次交互。AI 判定其操作效率比理想路径低 60%，建议优化手绘草图的按钮对比度。"
                ),
                accent = Color(0xFF2196F3)
            )

            ReportSection(
                title = "3. 易学性 (Learnability)",
                body = listOf(
                    "图标识别度：登录按钮（高），详情图交互（低）。",
                    "AI 分析：用户首次进入页面平均需 4.5s 锁定关键操作区。"
                ),
                accent = Color(0xFFFF9800)
            )

            ReportSection(
                title = "4. 容错性 (Error Tolerance)",
                body = listOf(
                    "误触率：8%（发生于详情页大图）。",
                    "恢复耗时：1.5s。面部表情显示用户在误触后迅速从困惑中恢复，找到了“确认”按钮。"
                ),
                accent = Color(0xFFF44336)
            )

            ReportSection(
                title = "5. 满意度 (Satisfaction)",
                body = listOf(
                    "面部流：捕捉到 2 次负面表情峰值（对应误触）和 1 次显著的正向峰值（对应 AI 自动识别成功）。",
                    "语音流：识别到关键吐槽词“点不动”，已自动关联至对应坐标点 (X=0.5, Y=0.4)。",
                    "建议：V2 版本需在图片区增加交互热区，以匹配用户“所见即所点”的直觉。"
                ),
                accent = Color(0xFF6A1B9A)
            )

            Spacer(modifier = Modifier.padding(bottom = 12.dp))
        }
    }
}

@Composable
private fun ReportSection(
    title: String,
    body: List<String>,
    accent: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFEFF)),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accent,
                                accent.copy(alpha = 0.55f)
                            )
                        )
                    )
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                body.forEach { line ->
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

private fun buildReportContent(): String {
    return """
PaperProto-AI 深度评测报告 (Session #0824)

1. 有效性 (Effectiveness)
任务完成率：100%（成功达成确认目标）。
AI 洞察：用户虽然发生误触，但最终独立修正了路径，未发生任务中断。

2. 效率 (Efficiency)
犹豫时长：15.4s。主要断点发生在登录页（寻找按钮）。
操作量：12 次交互。AI 判定其操作效率比理想路径低 60%，建议优化手绘草图的按钮对比度。

3. 易学性 (Learnability)
图标识别度：登录按钮（高），详情图交互（低）。
AI 分析：用户首次进入页面平均需 4.5s 锁定关键操作区。

4. 容错性 (Error Tolerance)
误触率：8%（发生于详情页大图）。
恢复耗时：1.5s。面部表情显示用户在误触后迅速从困惑中恢复，找到了“确认”按钮。

5. 满意度 (Satisfaction)
面部流：捕捉到 2 次负面表情峰值（对应误触）和 1 次显著的正向峰值（对应 AI 自动识别成功）。
语音流：识别到关键吐槽词“点不动”，已自动关联至对应坐标点 (X=0.5, Y=0.4)。
建议：V2 版本需在图片区增加交互热区，以匹配用户“所见即所点”的直觉。
""".trimIndent()
}

private fun saveReportToDownloads(
    context: Context,
    fileName: String,
    content: String
): Boolean {
    return try {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")
            }
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return false
        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.toByteArray())
        } ?: return false
        true
    } catch (_: Exception) {
        false
    }
}
