package com.example.prototypetesting.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prototypetesting.data.TimelineData
import com.example.prototypetesting.data.TimelineEvent
import com.example.prototypetesting.ui.theme.*

@Composable
fun TimelineView(
    timelineData: TimelineData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "行为时间轴",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "总时长 ${timelineData.formattedDuration}",
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 图例
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = Color(0xFF2196F3), label = "用户操作")
                LegendItem(color = Color(0xFF9C27B0), label = "心理状态")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 可滚动的时间轴
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                TimelineContent(timelineData = timelineData)
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun TimelineContent(timelineData: TimelineData) {
    val eventSpacing = 120.dp
    val totalWidth = eventSpacing * (timelineData.events.size - 1) + 80.dp

    Column(
        modifier = Modifier.width(totalWidth)
    ) {
        // 上方：用户操作区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            timelineData.events.forEachIndexed { index, event ->
                Box(
                    modifier = Modifier.width(if (index == 0) 40.dp else eventSpacing),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (index > 0) {
                        Spacer(modifier = Modifier.width(eventSpacing - 80.dp))
                    }
                    event.userAction?.let { action ->
                        UserActionCard(action = action)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 中间：时间轴线和节点
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            // 时间轴线
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                val y = size.height / 2
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(40f, y),
                    end = Offset(size.width - 40f, y),
                    strokeWidth = 3f
                )
            }

            // 时间节点
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                timelineData.events.forEachIndexed { index, event ->
                    Box(
                        modifier = Modifier.width(if (index == 0) 40.dp else eventSpacing),
                        contentAlignment = Alignment.Center
                    ) {
                        TimeNode(event = event)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 下方：用户状态区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            verticalAlignment = Alignment.Top
        ) {
            timelineData.events.forEachIndexed { index, event ->
                Box(
                    modifier = Modifier.width(if (index == 0) 40.dp else eventSpacing),
                    contentAlignment = Alignment.TopCenter
                ) {
                    event.userState?.let { state ->
                        UserStateCard(state = state)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeNode(event: TimelineEvent) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = getEmotionColor(event.userState?.emotion),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getActionIcon(event.userAction?.type),
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = event.formattedTime,
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun UserActionCard(action: com.example.prototypetesting.data.UserAction) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = getActionIcon(action.type),
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.target,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = action.description,
                fontSize = 9.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun UserStateCard(state: com.example.prototypetesting.data.UserState) {
    val emotionColor = getEmotionColor(state.emotion)
    Card(
        modifier = Modifier
            .width(100.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = emotionColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getEmotionEmoji(state.emotion),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getEmotionLabel(state.emotion),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = emotionColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = state.description,
                fontSize = 9.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )
        }
    }
}

private fun getActionIcon(type: String?): ImageVector {
    return when (type) {
        "click" -> Icons.Default.TouchApp
        "input" -> Icons.Default.Keyboard
        "scroll" -> Icons.Default.SwipeVertical
        "misclick" -> Icons.Default.ErrorOutline
        "start" -> Icons.Default.PlayArrow
        "end" -> Icons.Default.Stop
        else -> Icons.Default.Circle
    }
}

private fun getEmotionColor(emotion: String?): Color {
    return when (emotion) {
        "happy", "positive", "satisfied" -> Color(0xFF4CAF50)
        "interested", "focused" -> Color(0xFF2196F3)
        "neutral", "relieved" -> Color(0xFF9E9E9E)
        "confused" -> Color(0xFFFF9800)
        "frustrated" -> Color(0xFFF44336)
        else -> Color(0xFF9C27B0)
    }
}

private fun getEmotionEmoji(emotion: String?): String {
    return when (emotion) {
        "happy" -> "\uD83D\uDE04"
        "positive" -> "\uD83D\uDE0A"
        "satisfied" -> "\uD83D\uDE0C"
        "interested" -> "\uD83E\uDD14"
        "focused" -> "\uD83E\uDDD0"
        "neutral" -> "\uD83D\uDE10"
        "relieved" -> "\uD83D\uDE0C"
        "confused" -> "\uD83D\uDE15"
        "frustrated" -> "\uD83D\uDE23"
        else -> "\uD83D\uDE10"
    }
}

private fun getEmotionLabel(emotion: String?): String {
    return when (emotion) {
        "happy" -> "开心"
        "positive" -> "积极"
        "satisfied" -> "满意"
        "interested" -> "感兴趣"
        "focused" -> "专注"
        "neutral" -> "平静"
        "relieved" -> "释然"
        "confused" -> "困惑"
        "frustrated" -> "沮丧"
        else -> "未知"
    }
}
