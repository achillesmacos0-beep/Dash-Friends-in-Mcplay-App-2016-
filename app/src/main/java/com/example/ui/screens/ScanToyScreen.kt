package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.GameType
import com.example.data.Toy
import com.example.data.ToyRepository
import com.example.ui.theme.*
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScanToyScreen(
    unlockedToyIds: Set<String>,
    onToyUnlocked: (String) -> Unit,
    onNavigateCollection: () -> Unit,
    onPlayGame: (GameType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var selectedScanTarget by remember {
        mutableStateOf(ToyRepository.allToys.firstOrNull { !unlockedToyIds.contains(it.id) } ?: ToyRepository.allToys.first())
    }

    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var scanSuccessToy by remember { mutableStateOf<Toy?>(null) }

    // Animated laser scan line
    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Surface(
            color = McPlayRed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Scan Your Toy",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Point camera or select toy to scan",
                        color = McPlayYellow,
                        fontSize = 12.sp
                    )
                }

                if (!hasCameraPermission) {
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = McPlayYellow),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("enable_camera_button")
                    ) {
                        Text("Enable Camera", color = McPlayTextDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Camera Viewfinder / Hologram Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                // Live CameraX Preview
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview
                                )
                            } catch (_: Exception) {}
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Holographic Scanner Simulator Canvas
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = selectedScanTarget.imageRes),
                        contentDescription = "Toy to scan",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(190.dp)
                            .padding(8.dp)
                    )
                    Text(
                        text = "Target: ${selectedScanTarget.name}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // HUD Reticle Overlay
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .border(2.dp, McPlayGreen.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                // Animated Laser Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (220 * laserOffsetY).dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, McPlayGreen, McPlayYellow, McPlayGreen, Color.Transparent)
                            )
                        )
                )

                // Corner indicators
                Text(
                    text = "⌜       ⌝",
                    color = McPlayYellow,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Text(
                    text = "⌞       ⌟",
                    color = McPlayYellow,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // Scan status banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = if (isScanning) "SCANNING... ${(scanProgress * 100).toInt()}%" else "ALIGN TOY IN VIEWFINDER",
                    color = if (isScanning) McPlayYellow else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Toy Selector Carousel (Quick selection of toys to scan)
        Surface(
            color = Color(0xFF2A2A2A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "CHOOSE HAPPY MEAL TOY TO SCAN:",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ToyRepository.allToys) { toy ->
                        val isSelected = toy.id == selectedScanTarget.id
                        val isUnlocked = unlockedToyIds.contains(toy.id)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) McPlayYellow else Color(0xFF383838),
                            border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null,
                            modifier = Modifier
                                .width(90.dp)
                                .clickable {
                                    selectedScanTarget = toy
                                    SoundManager.playClick()
                                }
                                .testTag("scan_target_${toy.id}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Box {
                                    Image(
                                        painter = painterResource(id = toy.imageRes),
                                        contentDescription = toy.name,
                                        modifier = Modifier.size(50.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    if (isUnlocked) {
                                        Surface(
                                            shape = CircleShape,
                                            color = McPlayGreen,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .align(Alignment.TopEnd)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = toy.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = if (isSelected) McPlayTextDark else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SCAN BUTTON
                Button(
                    onClick = {
                        if (isScanning) return@Button
                        isScanning = true
                        scanProgress = 0f
                        SoundManager.playScanBeep()

                        coroutineScope.launch {
                            for (i in 1..10) {
                                delay(120)
                                scanProgress = i / 10f
                                if (i % 3 == 0) SoundManager.playScanBeep()
                            }
                            isScanning = false
                            onToyUnlocked(selectedScanTarget.id)
                            scanSuccessToy = selectedScanTarget
                            SoundManager.playUnlockFanfare()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("execute_scan_toy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isScanning) "SCANNING TOY..." else "SCAN THIS TOY NOW!",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Success Unlock Celebration Modal
    val successToy = scanSuccessToy
    if (successToy != null) {
        Dialog(onDismissRequest = { scanSuccessToy = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("scan_success_dialog")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "🎉 TOY UNLOCKED! 🎉",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = McPlayRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(McPlayPeachBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = successToy.imageRes),
                            contentDescription = successToy.name,
                            modifier = Modifier.size(140.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = successToy.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = McPlayTextDark,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Added to your Happy Meal Toy Collection!",
                        fontSize = 13.sp,
                        color = McPlayGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            val game = successToy.associatedGame
                            scanSuccessToy = null
                            onPlayGame(game)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = McPlayYellow),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("play_unlocked_toy_game_button")
                    ) {
                        Text(
                            text = "🎮 Play ${successToy.associatedGame.title}",
                            color = McPlayTextDark,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            scanSuccessToy = null
                            onNavigateCollection()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View in Collection", color = McPlayDeepBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
