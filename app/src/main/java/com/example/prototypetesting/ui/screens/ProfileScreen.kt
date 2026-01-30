package com.example.prototypetesting.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.data.SampleData
import com.example.prototypetesting.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        item {
            ProfileHeader()
        }
        
        item {
            UsageStatsSection()
        }
        
        item {
            UpgradeCard()
        }
        
        item {
            SettingsSection(isDarkMode) { isDarkMode = it }
        }
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
                .size(48.dp)
                .offset(x = 300.dp, y = 50.dp)
                .background(TextWhite.copy(alpha = 0.05f), CircleShape)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .background(TextWhite.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = TextWhite
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(TextWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "张",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = SampleData.userProfile.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            
            Text(
                text = SampleData.userProfile.email,
                fontSize = 13.sp,
                color = TextWhite.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TextWhite.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SampleData.userProfile.accountType,
                        fontSize = 12.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun UsageStatsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "使用统计",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UsageStatItem(
                    icon = Icons.Default.Description,
                    count = SampleData.userProfile.projectCount.toString(),
                    label = "项目",
                    iconColor = IconBlue
                )
                UsageStatItem(
                    icon = Icons.Default.Group,
                    count = SampleData.userProfile.testCount.toString(),
                    label = "测试",
                    iconColor = IconCyan
                )
                UsageStatItem(
                    icon = Icons.Default.SmartToy,
                    count = SampleData.userProfile.agentCount.toString(),
                    label = "Agent",
                    iconColor = IconOrange
                )
                UsageStatItem(
                    icon = Icons.Default.Assessment,
                    count = SampleData.userProfile.reportCount.toString(),
                    label = "报告",
                    iconColor = IconGreen
                )
            }
        }
    }
}

@Composable
fun UsageStatItem(
    icon: ImageVector,
    count: String,
    label: String,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun UpgradeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-10).dp)
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "升级到旗舰版",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "解锁无限项目、高级分析和团队协作",
                    fontSize = 13.sp,
                    color = TextWhite.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "了解更多",
                        color = PrimaryBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(isDarkMode: Boolean, onDarkModeChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        SettingItem(
            icon = Icons.Default.Settings,
            title = "设置",
            subtitle = "账号与偏好设置",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingItem(
            icon = Icons.Default.Notifications,
            title = "通知",
            subtitle = "管理推送通知",
            badge = "3",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingItemWithSwitch(
            icon = Icons.Default.DarkMode,
            title = "深色模式",
            subtitle = "当前：关闭",
            isChecked = isDarkMode,
            onCheckedChange = onDarkModeChange
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingItem(
            icon = Icons.Default.Language,
            title = "语言",
            subtitle = "简体中文",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingItem(
            icon = Icons.Default.Security,
            title = "隐私与安全",
            subtitle = "数据保护设置",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingItem(
            icon = Icons.Default.Help,
            title = "帮助与反馈",
            subtitle = "",
            onClick = { }
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(StatusRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        fontSize = 11.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextWhite,
                    checkedTrackColor = PrimaryBlue,
                    uncheckedThumbColor = TextWhite,
                    uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
                )
            )
        }
    }
}
