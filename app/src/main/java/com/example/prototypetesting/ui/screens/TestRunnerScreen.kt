package com.example.prototypetesting.ui.screens

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestRunnerScreen(
    navController: NavController,
    projectName: String,
    imageUris: List<String>,
    testType: String = "user"
) {
    var currentScreenIndex by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }
    var clickCount by remember { mutableStateOf(0) }
    var showComplete by remember { mutableStateOf(false) }
    var agentThinking by remember { mutableStateOf(false) }
    var agentThought by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    
    val agentThoughts = listOf(
        "正在分析页面布局结构...",
        "识别到输入框控件，准备进行输入测试",
        "检测到按钮元素，分析其点击响应",
        "评估页面跳转逻辑是否合理",
        "测试流程即将完成，准备生成报告"
    )
    
    LaunchedEffect(isRunning, isPaused) {
        if (isRunning && !isPaused) {
            while (true) {
                delay(1000)
                elapsedTime++
            }
        }
    }
    
    LaunchedEffect(isRunning, testType) {
        if (testType == "agent" && isRunning && !isPaused) {
            repeat(5) { step ->
                delay(4000)
                agentThinking = true
                agentThought = agentThoughts[step % agentThoughts.size]
                
                delay(2000)
                agentThinking = false
                clickCount++
                
                if (currentScreenIndex < imageUris.size - 1) {
                    delay(500)
                    currentScreenIndex++
                }
            }
            showComplete = true
        }
    }
    
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
    
    if (showComplete) {
        TestCompleteScreen(
            testType = testType,
            elapsedTime = elapsedTime,
            clickCount = clickCount,
            screenCount = imageUris.size,
            onViewReport = {
                navController.navigate("test_report/$projectName")
            }
        )
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (testType == "agent") "Agent 测试中" else "用户测试",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = projectName,
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
                    if (isRunning) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "录制中",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TextWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            formatTime(elapsedTime),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            clickCount.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    "${currentScreenIndex + 1} / ${imageUris.size}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            if (testType == "agent" && agentThinking) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            agentThought,
                            fontSize = 12.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(0.56f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TextWhite)
                        .clickable(enabled = isRunning && !isPaused && testType == "user") {
                            clickCount++
                            if (currentScreenIndex < imageUris.size - 1) {
                                scope.launch {
                                    delay(300)
                                    currentScreenIndex++
                                }
                            }
                        }
                ) {
                    if (imageUris.isNotEmpty() && currentScreenIndex < imageUris.size) {
                        AsyncImage(
                            model = Uri.parse(imageUris[currentScreenIndex]),
                            contentDescription = "测试页面",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TextWhite)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(imageUris) { index, uri ->
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .aspectRatio(0.6f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (index == currentScreenIndex) 2.dp else 1.dp,
                                color = if (index == currentScreenIndex) PrimaryBlue else TextSecondary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        AsyncImage(
                            model = Uri.parse(uri),
                            contentDescription = "页面${index + 1}",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TextWhite)
                    .padding(16.dp)
            ) {
                if (!isRunning) {
                    Button(
                        onClick = { isRunning = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (testType == "agent") "开始Agent测试" else "开始测试",
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isPaused) "继续" else "暂停")
                        }
                        Button(
                            onClick = { showComplete = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("完成测试")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestCompleteScreen(
    testType: String,
    elapsedTime: Int,
    clickCount: Int,
    screenCount: Int,
    onViewReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            if (testType == "agent") "Agent测试完成" else "用户测试完成",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            "测试数据已记录，可以查看详细报告",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TextWhite)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${elapsedTime / 60}:${(elapsedTime % 60).toString().padStart(2, '0')}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        "测试时长",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        clickCount.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        "点击次数",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        screenCount.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        "访问页面",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onViewReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("查看测试报告", fontSize = 15.sp)
        }
    }
}
