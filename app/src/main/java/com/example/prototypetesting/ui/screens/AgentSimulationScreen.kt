package com.example.prototypetesting.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.data.*
import com.example.prototypetesting.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentSimulationScreen(
    navController: NavController,
    personaType: String = "elderly"
) {
    val persona = when (personaType) {
        "elderly" -> AgentPersona.ELDERLY
        "youth" -> AgentPersona.YOUTH
        else -> AgentPersona.ELDERLY
    }
    val script = SimulationScripts.getScript(persona)

    var isRunning by remember { mutableStateOf(false) }
    var currentActionIndex by remember { mutableIntStateOf(-1) }
    var currentLogIndex by remember { mutableIntStateOf(-1) }
    var cursorPosition by remember { mutableStateOf(Offset(0.5f, 0.1f)) }
    var showCursor by remember { mutableStateOf(false) }

    // 伪 UI 状态
    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var activeField by remember { mutableStateOf<String?>(null) }
    var toastMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var isTyping by remember { mutableStateOf(false) }

    // 抖动动画
    var jitterOffset by remember { mutableStateOf(Offset.Zero) }

    // 思维链日志
    val visibleLogs = remember { mutableStateListOf<ThinkingLog>() }
    val logListState = rememberLazyListState()

    // 动画
    val cursorAnimX = remember { Animatable(0.5f) }
    val cursorAnimY = remember { Animatable(0.1f) }
    val coroutineScope = rememberCoroutineScope()

    // 剧本执行器
    LaunchedEffect(isRunning) {
        if (isRunning) {
            showCursor = true
            currentActionIndex = 0
            currentLogIndex = 0

            for ((index, action) in script.actions.withIndex()) {
                currentActionIndex = index

                // 同步显示思维链日志
                if (currentLogIndex < script.thinkingLogs.size) {
                    visibleLogs.add(script.thinkingLogs[currentLogIndex])
                    currentLogIndex++
                    // 自动滚动到底部
                    if (visibleLogs.isNotEmpty()) {
                        logListState.animateScrollToItem(visibleLogs.size - 1)
                    }
                }

                when (action) {
                    is SimulationAction.MoveTo -> {
                        // 移动光标
                        cursorAnimX.animateTo(
                            targetValue = action.target.x,
                            animationSpec = tween(
                                durationMillis = persona.cursorSpeed.toInt(),
                                easing = if (persona == AgentPersona.ELDERLY) LinearEasing else FastOutSlowInEasing
                            )
                        )
                        cursorAnimY.animateTo(
                            targetValue = action.target.y,
                            animationSpec = tween(
                                durationMillis = persona.cursorSpeed.toInt(),
                                easing = if (persona == AgentPersona.ELDERLY) LinearEasing else FastOutSlowInEasing
                            )
                        )
                        cursorPosition = Offset(cursorAnimX.value, cursorAnimY.value)
                    }

                    is SimulationAction.Jitter -> {
                        if (persona.hasJitter) {
                            // 模拟手抖
                            repeat(5) {
                                val angle = Random.nextFloat() * 360f
                                val distance = Random.nextFloat() * 0.02f
                                jitterOffset = Offset(
                                    cos(Math.toRadians(angle.toDouble())).toFloat() * distance,
                                    sin(Math.toRadians(angle.toDouble())).toFloat() * distance
                                )
                                delay(100L)
                            }
                            jitterOffset = Offset.Zero
                        }
                    }

                    is SimulationAction.Click -> {
                        // 点击效果
                        activeField = action.targetName
                        delay(200L)
                    }

                    is SimulationAction.TypeText -> {
                        isTyping = true
                        // 逐字输入
                        for (char in action.text) {
                            when (activeField) {
                                "用户名输入框" -> usernameText += char
                                "密码输入框" -> passwordText += char
                            }
                            delay(persona.typeSpeed)
                        }
                        isTyping = false
                    }

                    is SimulationAction.Wait -> {
                        delay(action.durationMs)
                    }

                    is SimulationAction.ShowToast -> {
                        toastMessage = action.message to action.isError
                        delay(2000L)
                        toastMessage = null
                    }
                }
            }

            // 显示剩余日志
            while (currentLogIndex < script.thinkingLogs.size) {
                visibleLogs.add(script.thinkingLogs[currentLogIndex])
                currentLogIndex++
                delay(500L)
                if (visibleLogs.isNotEmpty()) {
                    logListState.animateScrollToItem(visibleLogs.size - 1)
                }
            }

            delay(1000L)
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        // 顶部栏
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = persona.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${persona.displayName} 仿真测试", fontSize = 18.sp)
                }
            },
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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 左侧：仿真舞台
            Column(
                modifier = Modifier.weight(0.45f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "仿真舞台",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SimulationStage(
                    cursorPosition = Offset(
                        cursorAnimX.value + jitterOffset.x,
                        cursorAnimY.value + jitterOffset.y
                    ),
                    showCursor = showCursor,
                    usernameText = usernameText,
                    passwordText = passwordText,
                    activeField = activeField,
                    toastMessage = toastMessage,
                    isTyping = isTyping
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 开始按钮
                Button(
                    onClick = {
                        // 重置状态
                        usernameText = ""
                        passwordText = ""
                        activeField = null
                        toastMessage = null
                        visibleLogs.clear()
                        currentLogIndex = 0
                        coroutineScope.launch {
                            cursorAnimX.snapTo(0.5f)
                            cursorAnimY.snapTo(0.1f)
                        }
                        isRunning = true
                    },
                    enabled = !isRunning,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) TextSecondary else PrimaryBlue
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.PlayArrow else Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "仿真进行中..." else "开始仿真",
                        fontSize = 14.sp
                    )
                }
            }

            // 右侧：思维链日志
            Column(
                modifier = Modifier.weight(0.55f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Agent 思维链",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    LazyColumn(
                        state = logListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(visibleLogs) { log ->
                            ThinkingLogItem(log = log)
                        }

                        if (isRunning) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "> ",
                                        color = Color(0xFF00FF00),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    BlinkingCursor()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulationStage(
    cursorPosition: Offset,
    showCursor: Boolean,
    usernameText: String,
    passwordText: String,
    activeField: String?,
    toastMessage: Pair<String, Boolean>?,
    isTyping: Boolean
) {
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .aspectRatio(9f / 16f)
            .fillMaxHeight(0.7f)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 伪 UI 内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Logo
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryBlue, Color(0xFF64B5F6))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "用户登录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 用户名输入框 (位置约 0.3)
                OutlinedTextField(
                    value = usernameText,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("用户名", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = TextPrimary,
                        disabledBorderColor = if (activeField == "用户名输入框") PrimaryBlue else Color(0xFFE0E0E0),
                        disabledLabelColor = TextSecondary
                    ),
                    trailingIcon = {
                        if (activeField == "用户名输入框" && isTyping) {
                            BlinkingCursor(color = TextPrimary)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 密码输入框 (位置约 0.45)
                OutlinedTextField(
                    value = "*".repeat(passwordText.length),
                    onValueChange = {},
                    enabled = false,
                    label = { Text("密码", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = TextPrimary,
                        disabledBorderColor = if (activeField == "密码输入框") PrimaryBlue else Color(0xFFE0E0E0),
                        disabledLabelColor = TextSecondary
                    ),
                    trailingIcon = {
                        if (activeField == "密码输入框" && isTyping) {
                            BlinkingCursor(color = TextPrimary)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 登录按钮 (位置约 0.62)
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = PrimaryBlue,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "登 录", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "忘记密码?",
                    fontSize = 12.sp,
                    color = PrimaryBlue
                )
            }

            // Toast 消息
            toastMessage?.let { (message, isError) ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .background(
                            if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isError) Color(0xFFF44336) else Color(0xFF4CAF50),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = if (isError) Color(0xFFC62828) else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 光标 (手指图标)
            if (showCursor) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val constraints = this
                    val offsetX = (cursorPosition.x * constraints.maxWidth.value).dp
                    val offsetY = (cursorPosition.y * constraints.maxHeight.value).dp

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(offsetX.roundToPx(), offsetY.roundToPx()) }
                    ) {
                        // 手指图标
                        Text(
                            text = "\uD83D\uDC46",
                            fontSize = 28.sp,
                            modifier = Modifier.offset(x = (-14).dp, y = (-28).dp)
                        )
                        // 点击涟漪效果
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = (-6).dp, y = (-6).dp)
                                .background(
                                    PrimaryBlue.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingLogItem(log: ThinkingLog) {
    val (prefix, color) = when (log.type) {
        ThinkingLogType.OBSERVATION -> "[观察]" to Color(0xFF4FC3F7)
        ThinkingLogType.ANALYSIS -> "[分析]" to Color(0xFFFFB74D)
        ThinkingLogType.EMOTION -> "[情感]" to Color(0xFFE57373)
        ThinkingLogType.DECISION -> "[决策]" to Color(0xFF81C784)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "> ",
            color = Color(0xFF00FF00),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = prefix,
            color = color,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = " ",
            fontSize = 12.sp
        )
        Text(
            text = log.content,
            color = Color(0xFFE0E0E0),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun BlinkingCursor(color: Color = Color(0xFF00FF00)) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Text(
        text = "_",
        color = color.copy(alpha = alpha),
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
    )
}
