package com.example.prototypetesting.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototypetesting.data.SampleData
import com.example.prototypetesting.data.TestRecord
import com.example.prototypetesting.data.TestStatus
import com.example.prototypetesting.data.TestType
import com.example.prototypetesting.ui.theme.*
import com.example.prototypetesting.ui.components.ShareTestDialog

@Composable
fun TestManagementScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf(0) }
    var showShareDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<com.example.prototypetesting.data.Project?>(null) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
    ) {
        item {
            TestManagementHeader()
        }
        
        item {
            SearchBar()
        }
        
        item {
            FilterSection(selectedFilter) { selectedFilter = it }
        }
        
        item {
            QuickShareSection(
                onShareProject = { project ->
                    selectedProject = project
                    showShareDialog = true
                }
            )
        }
        
        item {
            TestRecordsSection()
        }
    }
    
    if (showShareDialog && selectedProject != null) {
        ShareTestDialog(
            project = selectedProject!!,
            onDismiss = {
                showShareDialog = false
                selectedProject = null
            }
        )
    }
}

@Composable
fun TestManagementHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
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
                .offset(x = 300.dp, y = 30.dp)
                .background(TextWhite.copy(alpha = 0.05f), CircleShape)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "测试管理",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "管理和追踪测试会话",
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
            
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextWhite.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "发起测试",
                    color = TextWhite,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp),
        placeholder = {
            Text(
                text = "搜索项目...",
                color = TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextSecondary
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = TextWhite,
            focusedContainerColor = TextWhite,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = PrimaryBlue
        )
    )
}

@Composable
fun FilterSection(selectedFilter: Int, onFilterSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-10).dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            label = "全部",
            count = "3",
            isSelected = selectedFilter == 0,
            onClick = { onFilterSelected(0) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            label = "待测试",
            count = "1",
            isSelected = selectedFilter == 1,
            onClick = { onFilterSelected(1) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            label = "进行中",
            count = "0",
            isSelected = selectedFilter == 2,
            onClick = { onFilterSelected(2) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            label = "已完成",
            count = "2",
            isSelected = selectedFilter == 3,
            onClick = { onFilterSelected(3) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    count: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue else TextWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TextWhite else PrimaryBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (isSelected) TextWhite else TextSecondary
            )
        }
    }
}

@Composable
fun QuickShareSection(onShareProject: (com.example.prototypetesting.data.Project) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "快速分享测试",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SampleData.projects) { project ->
                ProjectTestCard(
                    project = project,
                    onShare = { onShareProject(project) }
                )
            }
        }
    }
}

@Composable
fun ProjectTestCard(
    project: com.example.prototypetesting.data.Project,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(BackgroundBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = project.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(32.dp)
                            .background(PrimaryBlue, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "开始测试",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TestRecordsSection() {
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
                text = "测试记录",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            TextButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "筛选",
                    color = PrimaryBlue,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SampleData.testRecords.forEach { record ->
            TestRecordItem(record = record)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TestRecordItem(record: TestRecord) {
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
                    .size(48.dp)
                    .background(
                        when (record.testType) {
                            TestType.USER_TEST -> IconCyan.copy(alpha = 0.2f)
                            TestType.AGENT_TEST -> IconOrange.copy(alpha = 0.2f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (record.testType) {
                        TestType.USER_TEST -> Icons.Default.Group
                        TestType.AGENT_TEST -> Icons.Default.SmartToy
                    },
                    contentDescription = null,
                    tint = when (record.testType) {
                        TestType.USER_TEST -> IconCyan
                        TestType.AGENT_TEST -> IconOrange
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.projectName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (record.testType == TestType.USER_TEST) "用户测试" else "Agent测试"} · ${record.testerName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when (record.status) {
                                    TestStatus.COMPLETED -> StatusGreen
                                    TestStatus.IN_PROGRESS -> IconBlue
                                    TestStatus.PENDING -> StatusOrange
                                },
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (record.status) {
                            TestStatus.COMPLETED -> "已完成"
                            TestStatus.IN_PROGRESS -> "进行中"
                            TestStatus.PENDING -> "待测试"
                        },
                        fontSize = 12.sp,
                        color = when (record.status) {
                            TestStatus.COMPLETED -> StatusGreen
                            TestStatus.IN_PROGRESS -> IconBlue
                            TestStatus.PENDING -> StatusOrange
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (record.status == TestStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = IconBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "报告",
                            fontSize = 12.sp,
                            color = IconBlue
                        )
                    }
                }
            }
        }
    }
}
