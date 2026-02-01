package com.example.prototypetesting.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.data.*
import com.example.prototypetesting.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var currentLogIndex by remember { mutableIntStateOf(-1) }
    var cursorPosition by remember { mutableStateOf(Offset(0.5f, 0.1f)) }
    var showCursor by remember { mutableStateOf(false) }

    // 购物车 UI 状态
    var showMenu by remember { mutableStateOf(false) }
    var isManageMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    var toastMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // 思维链日志
    val visibleLogs = remember { mutableStateListOf<ThinkingLog>() }
    var isLogExpanded by remember { mutableStateOf(true) }

    // 动画
    val cursorAnimX = remember { Animatable(0.5f) }
    val cursorAnimY = remember { Animatable(0.1f) }
    val coroutineScope = rememberCoroutineScope()

    // 剧本执行器
    LaunchedEffect(isRunning) {
        if (isRunning) {
            showCursor = true
            currentLogIndex = 0

            for (action in script.actions) {
                // 同步显示思维链日志
                if (currentLogIndex < script.thinkingLogs.size) {
                    visibleLogs.add(script.thinkingLogs[currentLogIndex])
                    currentLogIndex++
                }

                when (action) {
                    is SimulationAction.MoveTo -> {
                        cursorAnimX.animateTo(
                            targetValue = action.target.x,
                            animationSpec = tween(
                                durationMillis = persona.cursorSpeed.toInt(),
                                easing = FastOutSlowInEasing
                            )
                        )
                        cursorAnimY.animateTo(
                            targetValue = action.target.y,
                            animationSpec = tween(
                                durationMillis = persona.cursorSpeed.toInt(),
                                easing = FastOutSlowInEasing
                            )
                        )
                        cursorPosition = Offset(cursorAnimX.value, cursorAnimY.value)
                    }

                    is SimulationAction.Click -> {
                        delay(150L)
                    }

                    is SimulationAction.Wait -> {
                        delay(action.durationMs)
                    }

                    is SimulationAction.ShowToast -> {
                        toastMessage = action.message to action.isError
                        delay(1500L)
                        toastMessage = null
                    }

                    is SimulationAction.ShowMenu -> {
                        showMenu = action.show
                    }

                    is SimulationAction.ToggleManageMode -> {
                        isManageMode = action.enabled
                    }

                    is SimulationAction.SelectItem -> {
                        selectedItems = selectedItems + action.index
                    }

                    else -> {}
                }
            }

            // 显示剩余日志
            while (currentLogIndex < script.thinkingLogs.size) {
                visibleLogs.add(script.thinkingLogs[currentLogIndex])
                currentLogIndex++
                delay(400L)
            }

            delay(800L)
            isRunning = false
            showCursor = false
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 仿真舞台
            ShoppingCartStage(
                cursorPosition = Offset(cursorAnimX.value, cursorAnimY.value),
                showCursor = showCursor,
                showMenu = showMenu,
                isManageMode = isManageMode,
                selectedItems = selectedItems,
                toastMessage = toastMessage,
                modifier = Modifier.height(420.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 开始按钮
            Button(
                onClick = {
                    // 重置状态
                    showMenu = false
                    isManageMode = false
                    selectedItems = emptySet()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
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
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 可折叠的思维链日志
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column {
                    // 折叠头部
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color(0xFF00FF00),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Agent 思维链",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            if (visibleLogs.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${visibleLogs.size})",
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                        IconButton(
                            onClick = { isLogExpanded = !isLogExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isLogExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isLogExpanded) "收起" else "展开",
                                tint = Color(0xFF888888)
                            )
                        }
                    }

                    // 可折叠内容
                    AnimatedVisibility(
                        visible = isLogExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            visibleLogs.forEach { log ->
                                ThinkingLogItem(log = log)
                            }

                            if (isRunning) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
private fun ShoppingCartStage(
    cursorPosition: Offset,
    showCursor: Boolean,
    showMenu: Boolean,
    isManageMode: Boolean,
    selectedItems: Set<Int>,
    toastMessage: Pair<String, Boolean>?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 顶部栏
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "购物车",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    // 右上角更多按钮
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterEnd)
                    )
                }

                // 商品列表
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CartItemCard(
                        name = "无线蓝牙耳机",
                        price = "¥299",
                        originalPrice = "¥399",
                        imageColor = Color(0xFF5C6BC0),
                        isManageMode = isManageMode,
                        isSelected = selectedItems.contains(0)
                    )
                    CartItemCard(
                        name = "手机支架",
                        price = "¥39",
                        originalPrice = "¥59",
                        imageColor = Color(0xFF26A69A),
                        isManageMode = isManageMode,
                        isSelected = selectedItems.contains(1)
                    )
                    CartItemCard(
                        name = "充电宝 20000mAh",
                        price = "¥129",
                        originalPrice = "¥169",
                        imageColor = Color(0xFFEF5350),
                        isManageMode = isManageMode,
                        isSelected = selectedItems.contains(2)
                    )
                }

                // 底部操作栏
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    if (isManageMode) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFFF44336),
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text("删除所选", fontSize = 14.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = false,
                                    onCheckedChange = {},
                                    enabled = false,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("全选", fontSize = 12.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("合计: ", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    "¥467",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF44336)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = Color(0xFFF44336),
                                        disabledContentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 20.dp)
                                ) {
                                    Text("结算(3)", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 下拉菜单
            if (showMenu) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 8.dp)
                        .width(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("管理购物车", fontSize = 13.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Toast 消息
            toastMessage?.let { (message, isError) ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = Color.White
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
                        Text(
                            text = "\uD83D\uDC46",
                            fontSize = 24.sp,
                            modifier = Modifier.offset(x = (-12).dp, y = (-24).dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    name: String,
    price: String,
    originalPrice: String,
    imageColor: Color,
    isManageMode: Boolean,
    isSelected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isManageMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {},
                    enabled = false,
                    modifier = Modifier.size(20.dp),
                    colors = CheckboxDefaults.colors(
                        disabledCheckedColor = PrimaryBlue,
                        disabledUncheckedColor = Color(0xFFBDBDBD)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // 商品图片占位
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(imageColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = imageColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = originalPrice,
                        fontSize = 11.sp,
                        color = Color(0xFFBDBDBD),
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            // 数量选择器
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("-", fontSize = 14.sp, color = TextSecondary)
                Text(
                    "1",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text("+", fontSize = 14.sp, color = TextSecondary)
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

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "> ",
            color = Color(0xFF00FF00),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = prefix,
            color = color,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = " ${log.content}",
            color = Color(0xFFE0E0E0),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun BlinkingCursor() {
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
        color = Color(0xFF00FF00).copy(alpha = alpha),
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
    )
}
