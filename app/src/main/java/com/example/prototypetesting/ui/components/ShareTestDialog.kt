package com.example.prototypetesting.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.prototypetesting.data.Project
import com.example.prototypetesting.ui.theme.*

@Composable
fun ShareTestDialog(
    project: Project,
    onDismiss: () -> Unit
) {
    var recordProcess by remember { mutableStateOf(true) }
    var recordUser by remember { mutableStateOf(true) }
    var heatmap by remember { mutableStateOf(true) }
    var copied by remember { mutableStateOf(false) }
    
    val testLink = "https://prototest.app/test/${project.id}"
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TextWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "分享测试链接",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "分享\"${project.name}\"的测试链接",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundBlue
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = testLink,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                copied = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = "复制",
                                tint = if (copied) StatusGreen else PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareOptionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "复制链接",
                        onClick = { copied = true }
                    )
                    ShareOptionButton(
                        icon = Icons.Default.QrCode,
                        label = "二维码",
                        onClick = { }
                    )
                    ShareOptionButton(
                        icon = Icons.Default.Email,
                        label = "邮件",
                        onClick = { }
                    )
                    ShareOptionButton(
                        icon = Icons.Default.Share,
                        label = "更多",
                        onClick = { }
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "测试设置",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ToggleSettingItem(
                    label = "录制测试过程",
                    enabled = recordProcess,
                    onToggle = { recordProcess = !recordProcess }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ToggleSettingItem(
                    label = "录制用户状态",
                    enabled = recordUser,
                    onToggle = { recordUser = !recordUser }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ToggleSettingItem(
                    label = "记录点击热图",
                    enabled = heatmap,
                    onToggle = { heatmap = !heatmap }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = "发送测试邀请",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ShareOptionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(BackgroundBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun ToggleSettingItem(
    label: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundBlue.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextPrimary
            )
            
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(24.dp)
                    .background(
                        if (enabled) PrimaryBlue else TextSecondary.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(20.dp)
                        .background(TextWhite, CircleShape)
                )
            }
        }
    }
}
