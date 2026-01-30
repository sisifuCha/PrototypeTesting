package com.example.prototypetesting.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.data.Project
import com.example.prototypetesting.data.ProjectStatus
import com.example.prototypetesting.data.SampleData
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        item {
            HomeHeader()
        }
        
        item {
            StatsSection()
        }
        
        item {
            QuickActionsSection(navController)
        }
        
        item {
            CreateNewProjectCard(navController)
        }
        
        item {
            RecentProjectsSection(navController)
        }
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF5b9fe3),
                        androidx.compose.ui.graphics.Color(0xFF4088d9),
                        androidx.compose.ui.graphics.Color(0xFF2d6cb5)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .offset(x = 250.dp, y = (-30).dp)
                .background(TextWhite.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(64.dp)
                .offset(x = 300.dp, y = 40.dp)
                .background(TextWhite.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = 280.dp, y = 80.dp)
                .background(TextWhite.copy(alpha = 0.1f), CircleShape)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            TextWhite.copy(alpha = 0.25f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            TextWhite.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "原型测试",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "纸质原型快速验证",
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        TextWhite.copy(alpha = 0.25f),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        TextWhite.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "张",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-40).dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            count = "4",
            label = "项目总数",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            count = "1",
            label = "进行中",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            count = "12",
            label = "已完成",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(count: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBlueLight.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextWhite.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-20).dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Default.CameraAlt,
                label = "上传原型",
                backgroundColor = IconBlue,
                onClick = { navController.navigate(Screen.Upload.route) }
            )
            QuickActionButton(
                icon = Icons.Default.Group,
                label = "用户测试",
                backgroundColor = IconCyan,
                onClick = { navController.navigate(Screen.TestManagement.route) }
            )
            QuickActionButton(
                icon = Icons.Default.SmartToy,
                label = "Agent",
                backgroundColor = IconOrange,
                onClick = { navController.navigate(Screen.AgentTest.route) }
            )
            QuickActionButton(
                icon = Icons.Default.Description,
                label = "测试报告",
                backgroundColor = IconGreen,
                onClick = { }
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(backgroundColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextWhite,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextPrimary
        )
    }
}

@Composable
fun CreateNewProjectCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp)
            .clickable { navController.navigate(Screen.Upload.route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        PrimaryBlue.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "创建新项目",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "拍照上传纸质原型，AI自动识别",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun RecentProjectsSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近项目",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            TextButton(onClick = { }) {
                Text(
                    text = "全部",
                    color = PrimaryBlue,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SampleData.projects.forEach { project ->
            ProjectItem(project = project)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ProjectItem(project: Project) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(16.dp),
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
                    .size(60.dp)
                    .background(BackgroundBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (project.status == ProjectStatus.ONGOING) StatusGreen else TextSecondary,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (project.status == ProjectStatus.ONGOING) "就绪" else "已完成",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = project.createdDate,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}
