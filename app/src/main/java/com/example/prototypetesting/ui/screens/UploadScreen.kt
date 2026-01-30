package com.example.prototypetesting.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototypetesting.navigation.Screen
import com.example.prototypetesting.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(navController: NavController) {
    var projectName by remember { mutableStateOf("") }
    var uploadedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isAnalyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        Log.d("UploadScreen", "Selected ${uris.size} images")
        if (uris.isNotEmpty()) {
            uploadedImages = uploadedImages + uris
            Toast.makeText(context, "已选择 ${uris.size} 张图片", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "未选择图片", Toast.LENGTH_SHORT).show()
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // 这里简化处理，实际应该保存bitmap到Uri
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "上传原型",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "支持多种方式上传纸质原型",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TextWhite
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBlue)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (uploadedImages.isNotEmpty()) 80.dp else 0.dp)
            ) {
                item {
                    ProjectNameSection(projectName) { projectName = it }
                }
                
                item {
                    UploadMethodsSection(
                    onMethodSelect = { method ->
                        Log.d("UploadScreen", "Method selected: $method")
                        when (method) {
                            "camera" -> {
                                Toast.makeText(context, "打开相机", Toast.LENGTH_SHORT).show()
                                // cameraLauncher.launch(null)
                            }
                            "gallery" -> {
                                Toast.makeText(context, "打开相册", Toast.LENGTH_SHORT).show()
                                try {
                                    imagePickerLauncher.launch("image/*")
                                } catch (e: Exception) {
                                    Log.e("UploadScreen", "Error launching image picker", e)
                                    Toast.makeText(context, "打开相册失败: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                            "scan" -> {
                                Toast.makeText(context, "打开扫描", Toast.LENGTH_SHORT).show()
                                imagePickerLauncher.launch("image/*")
                            }
                            "file" -> {
                                Toast.makeText(context, "打开文件", Toast.LENGTH_SHORT).show()
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                    }
                )
                }
                
                item {
                    if (uploadedImages.isNotEmpty()) {
                        UploadedImagesSection(
                            images = uploadedImages,
                            onRemoveImage = { index ->
                                uploadedImages = uploadedImages.filterIndexed { i, _ -> i != index }
                            },
                            onAddMore = {
                                imagePickerLauncher.launch("image/*")
                            }
                        )
                    } else {
                        EmptyUploadArea()
                    }
                }
            }
            
            if (uploadedImages.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(TextWhite)
                        .padding(16.dp)
                ) {
                    AnalyzeButton(
                        isAnalyzing = isAnalyzing,
                        onClick = {
                            if (uploadedImages.isEmpty()) {
                                Toast.makeText(context, "请先上传图片", Toast.LENGTH_SHORT).show()
                                return@AnalyzeButton
                            }
                            isAnalyzing = true
                            Toast.makeText(context, "开始AI识别...", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                delay(2000)
                                isAnalyzing = false
                                val imageUrisString = uploadedImages.joinToString(",") { it.toString() }
                                val encodedUris = URLEncoder.encode(imageUrisString, StandardCharsets.UTF_8.toString())
                                val encodedName = URLEncoder.encode(projectName.ifEmpty { "未命名项目" }, StandardCharsets.UTF_8.toString())
                                Log.d("UploadScreen", "Navigating with images: $imageUrisString")
                                Log.d("UploadScreen", "Encoded: $encodedUris")
                                navController.navigate(
                                    "${Screen.ProjectDetail.route}/$encodedName/$encodedUris"
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectNameSection(projectName: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "项目名称",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = projectName,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("输入项目名称", color = TextSecondary) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = TextWhite,
                focusedContainerColor = TextWhite,
                unfocusedBorderColor = BackgroundBlue,
                focusedBorderColor = PrimaryBlue
            )
        )
    }
}

@Composable
fun UploadMethodsSection(onMethodSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "选择上传方式",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UploadMethodCard(
                icon = Icons.Default.CameraAlt,
                title = "拍照上传",
                description = "直接拍摄纸质原型",
                backgroundColor = IconBlue,
                modifier = Modifier.weight(1f),
                onClick = { onMethodSelect("camera") }
            )
            UploadMethodCard(
                icon = Icons.Default.Image,
                title = "相册选择",
                description = "从相册选择图片",
                backgroundColor = IconCyan,
                modifier = Modifier.weight(1f),
                onClick = { onMethodSelect("gallery") }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UploadMethodCard(
                icon = Icons.Default.DocumentScanner,
                title = "扫描文档",
                description = "扫描多页文档",
                backgroundColor = IconOrange,
                modifier = Modifier.weight(1f),
                onClick = { onMethodSelect("scan") }
            )
            UploadMethodCard(
                icon = Icons.Default.FolderOpen,
                title = "导入文件",
                description = "从文件夹导入",
                backgroundColor = IconGreen,
                modifier = Modifier.weight(1f),
                onClick = { onMethodSelect("file") }
            )
        }
    }
}

@Composable
fun UploadMethodCard(
    icon: ImageVector,
    title: String,
    description: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = backgroundColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = description,
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun UploadedImagesSection(
    images: List<Uri>,
    onRemoveImage: (Int) -> Unit,
    onAddMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "已上传 (${images.size} 张)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            TextButton(onClick = onAddMore) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加更多", fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(300.dp)
        ) {
            itemsIndexed(images) { index, uri ->
                ImagePreviewCard(
                    index = index,
                    imageUri = uri,
                    onRemove = { onRemoveImage(index) }
                )
            }
        }
        
        Text(
            text = "点击图片可删除",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun ImagePreviewCard(index: Int, imageUri: Uri, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundBlue)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "原型页面 ${index + 1}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
                .background(PrimaryBlue, CircleShape)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f))
        )
        
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "删除",
            tint = TextWhite,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun EmptyUploadArea() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TextWhite),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            PrimaryBlue.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(BackgroundBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "选择上方方式上传原型图片",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Text(
                text = "支持 JPG、PNG、GIF、WebP 格式",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AnalyzeButton(isAnalyzing: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isAnalyzing,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = TextWhite,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI 分析中...", fontSize = 15.sp)
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("开始 AI 识别", fontSize = 15.sp)
            }
        }
        
        Text(
            text = "AI 将自动识别组件和页面跳转关系",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}
