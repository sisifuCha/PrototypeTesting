package com.example.prototypetesting.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.data.ComponentType
import com.example.prototypetesting.data.UIComponent
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: NavController,
    projectName: String,
    imageUris: List<String>
) {
    var currentScreenIndex by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    val mockComponents = remember {
        listOf(
            UIComponent("1", ComponentType.INPUT, "AI识别: 搜索框", "输入框"),
            UIComponent("2", ComponentType.BUTTON, "AI识别: 今天吃啥", "按钮"),
            UIComponent("3", ComponentType.TEXT, "AI识别: 分类标签", "文本")
        )
    }
    
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
                        Text(
                            text = "页面 ${currentScreenIndex + 1}",
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
                    IconButton(onClick = {
                        val encodedUris = URLEncoder.encode(imageUris.joinToString(","), StandardCharsets.UTF_8.toString())
                        val encodedName = URLEncoder.encode(projectName, StandardCharsets.UTF_8.toString())
                        navController.navigate("${Screen.TestRunner.route}/$encodedName/$encodedUris/user")
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "开始测试", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.RemoveRedEye, contentDescription = "预览", tint = TextSecondary)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "分享", tint = TextSecondary)
                    }
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
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundBlue)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxHeight()
                        .background(TextWhite)
                        .padding(16.dp)
                ) {
                    Column {
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
                                AsyncImage(
                                    model = Uri.parse(imageUris[currentScreenIndex]),
                                    contentDescription = "页面预览",
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
                
                Box(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight()
                        .background(TextWhite)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "组件 (${mockComponents.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "添加组件",
                                    tint = PrimaryBlue
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(mockComponents) { component ->
                                ComponentItem(component)
                            }
                        }
                    }
                }
            }
        }
    }
    
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

@Composable
fun ComponentItem(component: UIComponent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundBlue.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(TextWhite, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = component.type.icon,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = component.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = component.description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
