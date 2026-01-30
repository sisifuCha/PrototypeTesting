package com.example.prototypetesting.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestReportScreen(
    navController: NavController,
    projectName: String
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val tabs = listOf("概览", "热力图", "时间线", "洞察")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "测试报告",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            projectName,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Download, contentDescription = "下载")
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = TextWhite,
                contentColor = PrimaryBlue,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 14.sp) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> OverviewTab()
                1 -> HeatmapTab()
                2 -> TimelineTab()
                3 -> InsightsTab()
            }
        }
    }
}

@Composable
fun OverviewTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TextWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "测试摘要",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("完成率", "95%", Icons.Default.CheckCircle)
                        StatItem("平均时长", "2:34", Icons.Default.AccessTime)
                        StatItem("点击数", "18", Icons.Default.TouchApp)
                    }
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TextWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "用户路径分析",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "查看详情",
                            fontSize = 12.sp,
                            color = PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PathItem("首页", "100%", true)
                    PathItem("搜索页", "85%", false)
                    PathItem("详情页", "72%", false)
                    PathItem("结果页", "58%", false)
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TextWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "关键发现",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    FindingItem(
                        "搜索框使用率高",
                        "92% 的用户首先点击了搜索功能",
                        "positive"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FindingItem(
                        "返回按钮位置需优化",
                        "35% 的用户在寻找返回按钮时出现犹豫",
                        "negative"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FindingItem(
                        "页面跳转流畅",
                        "平均页面切换时间 0.3 秒",
                        "positive"
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun PathItem(name: String, percentage: String, isFirst: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isFirst) PrimaryBlue else PrimaryBlue.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                percentage.replace("%", ""),
                fontSize = 11.sp,
                color = if (isFirst) TextWhite else PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(
                "$percentage 用户访问",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun FindingItem(title: String, description: String, type: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    if (type == "positive") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    CircleShape
                )
                .padding(top = 6.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                description,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun HeatmapTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Whatshot,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "热力图分析",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "显示用户点击热点区域",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun TimelineTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(5) { index ->
            TimelineItem(
                time = "00:${(index * 15).toString().padStart(2, '0')}",
                action = when (index % 3) {
                    0 -> "点击搜索框"
                    1 -> "输入文本"
                    else -> "页面跳转"
                },
                screen = "页面 ${index + 1}"
            )
        }
    }
}

@Composable
fun TimelineItem(time: String, action: String, screen: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    time,
                    fontSize = 10.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(action, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    screen,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun InsightsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InsightCard(
                title = "用户体验评分",
                score = "8.5",
                description = "整体用户体验良好，导航流畅",
                trend = "up"
            )
        }
        item {
            InsightCard(
                title = "任务完成率",
                score = "92%",
                description = "大部分用户能够顺利完成核心任务",
                trend = "up"
            )
        }
        item {
            InsightCard(
                title = "平均停留时长",
                score = "2:34",
                description = "用户在关键页面停留时间适中",
                trend = "neutral"
            )
        }
    }
}

@Composable
fun InsightCard(title: String, score: String, description: String, trend: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 14.sp, color = TextSecondary)
                Icon(
                    when (trend) {
                        "up" -> Icons.Default.TrendingUp
                        "down" -> Icons.Default.TrendingDown
                        else -> Icons.Default.TrendingFlat
                    },
                    contentDescription = null,
                    tint = when (trend) {
                        "up" -> MaterialTheme.colorScheme.primary
                        "down" -> MaterialTheme.colorScheme.error
                        else -> TextSecondary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                score,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                description,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}
