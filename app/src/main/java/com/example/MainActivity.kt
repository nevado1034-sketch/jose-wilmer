package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ClientEntity
import com.example.ui.LitioViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.ChatMessage

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.PathEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: LitioViewModel = viewModel()
                val context = LocalContext.current

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Bottom spacer for safe areas if needed
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        AppNavigation(
                            viewModel = viewModel,
                            onOpenUrl = { url ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No se pudo abrir WhatsApp. Verifica si está instalado.", Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: LitioViewModel,
    onOpenUrl: (String) -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordIncorrect by remember { mutableStateOf(false) }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false
                passwordInput = ""
                isPasswordIncorrect = false
            },
            title = {
                Text(
                    text = "Acceso Técnico/Admin ⚡",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ingresa la clave de administrador para acceder a las herramientas de control y sincronización:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { 
                            passwordInput = it
                            isPasswordIncorrect = false
                        },
                        label = { Text("Contraseña / PIN") },
                        singleLine = true,
                        isError = isPasswordIncorrect,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (passwordInput == "1034" || passwordInput.lowercase() == "litio") {
                                    viewModel.isAdminMode = true
                                    showPasswordDialog = false
                                    passwordInput = ""
                                    isPasswordIncorrect = false
                                } else {
                                    isPasswordIncorrect = true
                                }
                            }
                        ),
                        supportingText = {
                            if (isPasswordIncorrect) {
                                Text(
                                    text = "Clave incorrecta. Inténtalo de nuevo.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (passwordInput == "1034" || passwordInput.lowercase() == "litio") {
                            viewModel.isAdminMode = true
                            showPasswordDialog = false
                            passwordInput = ""
                            isPasswordIncorrect = false
                        } else {
                            isPasswordIncorrect = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPasswordDialog = false
                        passwordInput = ""
                        isPasswordIncorrect = false
                    }
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // App Header (Shared Title & Toggle)
        AppHeader(
            isAdminMode = viewModel.isAdminMode,
            onToggleMode = {
                if (viewModel.isAdminMode) {
                    viewModel.isAdminMode = false
                } else {
                    showPasswordDialog = true
                }
            }
        )

        // Switch Screens based on Mode & Registration state
        if (viewModel.isAdminMode) {
            AdminDashboard(viewModel = viewModel)
        } else {
            if (viewModel.isClientRegistered && viewModel.trackedClient != null) {
                ClientDashboard(
                    viewModel = viewModel,
                    onOpenUrl = onOpenUrl
                )
            } else {
                ClientWelcomeScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LitioBrandMark(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .graphicsLayer(alpha = 0.99f) // Required for BlendMode.Clear to work on an offscreen buffer
    ) {
        val width = size.width
        val height = size.height
        val cy = height * 0.45f
        
        // Let's draw the outline and bolt relative to a proportional scale
        val strokeWidth = width * 0.052f
        val gapClearWidth = strokeWidth + width * 0.06f
        
        // Skew slant matching the reference image's italics angle (more aggressive slant)
        val skew = 0.35f
        fun getSlant(y: Float): Float = (cy - y) * skew
        
        // Proportions for the slanted battery shell - wider and shorter to match the real logo aspect ratio
        val yTop = height * 0.22f
        val yBottom = height * 0.68f
        val xLeft = width * 0.10f
        val xRight = width * 0.74f
        
        // Proportions for the right terminal cap
        val yCapTop = height * 0.35f
        val yCapBottom = height * 0.55f
        val xCapRight = width * 0.88f
        
        val batteryPath = Path().apply {
            moveTo(xLeft + getSlant(yTop), yTop)
            lineTo(xRight + getSlant(yTop), yTop)
            lineTo(xRight + getSlant(yCapTop), yCapTop)
            lineTo(xCapRight + getSlant(yCapTop), yCapTop)
            lineTo(xCapRight + getSlant(yCapBottom), yCapBottom)
            lineTo(xRight + getSlant(yCapBottom), yCapBottom)
            lineTo(xRight + getSlant(yBottom), yBottom)
            lineTo(xLeft + getSlant(yBottom), yBottom)
            close()
        }
        
        val boltPath = Path().apply {
            // Perfect nested lightning bolt matching the provided image precisely
            moveTo(width * 0.38f, height * 0.04f)
            lineTo(width * 0.60f, height * 0.04f)
            lineTo(width * 0.51f, height * 0.38f)
            lineTo(width * 0.66f, height * 0.38f)
            lineTo(width * 0.39f, height * 0.96f)
            lineTo(width * 0.47f, height * 0.50f)
            lineTo(width * 0.26f, height * 0.50f)
            close()
        }
        
        // 1. Draw battery outline with rounded corners
        drawPath(
            path = batteryPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                miter = 4f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.cornerPathEffect(width * 0.065f)
            )
        )
        
        // 2. Clear battery outline under the bolt with a custom gap
        drawPath(
            path = boltPath,
            color = Color.Transparent,
            style = Stroke(
                width = gapClearWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.cornerPathEffect(width * 0.02f)
            ),
            blendMode = BlendMode.Clear
        )
        
        // 3. Clear battery outline under the body of the bolt
        drawPath(
            path = boltPath,
            color = Color.Transparent,
            style = Fill,
            blendMode = BlendMode.Clear
        )
        
        // 4. Draw filled bolt in the brand's primary color with slightly softened corners
        drawPath(
            path = boltPath,
            color = color,
            style = Fill
        )
    }
}

@Composable
fun LitioBrandLogo(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    subColor: Color = MaterialTheme.colorScheme.primary,
    showSlogan: Boolean = false,
    fontSize: Float = 20f
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val markSize = (fontSize * 2.3f).dp
        LitioBrandMark(
            modifier = Modifier.size(markSize),
            color = primaryColor
        )
        
        Spacer(modifier = Modifier.width((fontSize * 0.45f).dp))
        
        Column {
            val wordmarkHeight = (fontSize * 0.95f).dp
            val wordmarkWidth = (fontSize * 2.7f).dp
            
            Canvas(
                modifier = Modifier
                    .width(wordmarkWidth)
                    .height(wordmarkHeight)
                    .graphicsLayer(alpha = 0.99f)
            ) {
                val w = size.width
                val H = size.height
                val s = 0.38f // Match the sporty italic slant of the official logo
                val yBaseline = H * 0.90f
                val yTopAscender = H * 0.10f
                val yDotBottom = H * 0.28f
                val yXHeight = H * 0.54f
                
                // Calculate precise available width factoring in slant-induced horizontal shift
                val Shift = (yBaseline - yTopAscender) * s
                val padding = w * 0.03f
                val W_base = w - Shift - 2 * padding
                
                // Divide base width into exactly 8.0 units of the design grid
                val T = W_base / 8.0f
                val xStart = padding
                
                val S_w = T * 1.0f    // Stem width (consistent across all letters)
                val G = T * 0.35f      // Standard gap between separate letters
                val G_t = T * 0.50f    // Inner gap for 't-i2' connection block
                val G_o = T * 0.45f    // Inner horizontal cutout of 'o'
                
                fun slantedX(x: Float, y: Float): Float = x + (yBaseline - y) * s
                
                // Base-level start positions
                val x_l = xStart
                val x_i1 = x_l + S_w + G
                val x_t = x_i1 + S_w + G
                val x_i2 = x_t + S_w + G_t
                val x_o_l = x_i2 + S_w + G
                val x_o_r = x_o_l + S_w + G_o
                
                // 1. l - Solid slanted bar from baseline to top ascender
                val pathL = Path().apply {
                    moveTo(slantedX(x_l, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_l + S_w, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_l + S_w, yBaseline), yBaseline)
                    lineTo(slantedX(x_l, yBaseline), yBaseline)
                    close()
                }
                drawPath(path = pathL, color = primaryColor, style = Fill)
                
                // 2. i (first) - stem
                val pathI1Stem = Path().apply {
                    moveTo(slantedX(x_i1, yXHeight), yXHeight)
                    lineTo(slantedX(x_i1 + S_w, yXHeight), yXHeight)
                    lineTo(slantedX(x_i1 + S_w, yBaseline), yBaseline)
                    lineTo(slantedX(x_i1, yBaseline), yBaseline)
                    close()
                }
                drawPath(path = pathI1Stem, color = primaryColor, style = Fill)
                
                // 2. i (first) - separate dot
                val pathI1Dot = Path().apply {
                    moveTo(slantedX(x_i1, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_i1 + S_w, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_i1 + S_w, yDotBottom), yDotBottom)
                    lineTo(slantedX(x_i1, yDotBottom), yDotBottom)
                    close()
                }
                drawPath(path = pathI1Dot, color = primaryColor, style = Fill)
                
                // 3. t & i (second) - stems drawn as a solid block first
                val pathTI2Block = Path().apply {
                    moveTo(slantedX(x_t, yXHeight), yXHeight)
                    lineTo(slantedX(x_i2 + S_w, yXHeight), yXHeight)
                    lineTo(slantedX(x_i2 + S_w, yBaseline), yBaseline)
                    lineTo(slantedX(x_t, yBaseline), yBaseline)
                    close()
                }
                drawPath(path = pathTI2Block, color = primaryColor, style = Fill)
                
                // 3. Clear the cutout under the 't-i2' bridge with beautiful rounded top corners
                val bridgeThickness = S_w * 0.70f
                val yBridgeBottom = yXHeight + bridgeThickness
                val r = T * 0.20f // curve radius
                val pathCutout = Path().apply {
                    moveTo(slantedX(x_i2, yBaseline), yBaseline)
                    lineTo(slantedX(x_i2, yBridgeBottom + r), yBridgeBottom + r)
                    quadraticTo(
                        slantedX(x_i2, yBridgeBottom), yBridgeBottom,
                        slantedX(x_i2 - r, yBridgeBottom), yBridgeBottom
                    )
                    lineTo(slantedX(x_t + S_w + r, yBridgeBottom), yBridgeBottom)
                    quadraticTo(
                        slantedX(x_t + S_w, yBridgeBottom), yBridgeBottom,
                        slantedX(x_t + S_w, yBridgeBottom + r), yBridgeBottom + r
                    )
                    lineTo(slantedX(x_t + S_w, yBaseline), yBaseline)
                    close()
                }
                drawPath(
                    path = pathCutout,
                    color = Color.Transparent,
                    style = Fill,
                    blendMode = BlendMode.Clear
                )
                
                // 4. o - Draw outer solid body shape
                val pathOSolid = Path().apply {
                    moveTo(slantedX(x_o_l, yBaseline), yBaseline)
                    lineTo(slantedX(x_o_l, yXHeight), yXHeight)
                    lineTo(slantedX(x_o_l + S_w, yXHeight), yXHeight)
                    lineTo(slantedX(x_o_r, yXHeight), yXHeight)
                    lineTo(slantedX(x_o_r, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_o_r + S_w, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_o_r + S_w, yBaseline), yBaseline)
                    close()
                }
                drawPath(path = pathOSolid, color = primaryColor, style = Fill)
                
                // 5. top bar - Draw the top ligature bar starting above second i and covering o
                val pathTopBar = Path().apply {
                    moveTo(slantedX(x_i2, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_o_r + S_w, yTopAscender), yTopAscender)
                    lineTo(slantedX(x_o_r + S_w, yDotBottom), yDotBottom)
                    lineTo(slantedX(x_i2, yDotBottom), yDotBottom)
                    close()
                }
                drawPath(path = pathTopBar, color = primaryColor, style = Fill)
                
                // 6. Clear the horizontal channel above the second i stem that flows into o
                val pathChannel = Path().apply {
                    moveTo(slantedX(x_i2, yDotBottom), yDotBottom)
                    lineTo(slantedX(x_o_r, yDotBottom), yDotBottom)
                    lineTo(slantedX(x_o_r, yXHeight), yXHeight)
                    lineTo(slantedX(x_i2, yXHeight), yXHeight)
                    close()
                }
                drawPath(
                    path = pathChannel,
                    color = Color.Transparent,
                    style = Fill,
                    blendMode = BlendMode.Clear
                )
                
                // 7. Clear the lower inner cutout of o with elegant rounded bottom corners
                val yOInnerBottom = yBaseline - S_w * 0.70f
                val r_o = T * 0.15f
                val pathOInnerRounded = Path().apply {
                    moveTo(slantedX(x_o_l + S_w, yXHeight), yXHeight)
                    lineTo(slantedX(x_o_r, yXHeight), yXHeight)
                    lineTo(slantedX(x_o_r, yOInnerBottom - r_o), yOInnerBottom - r_o)
                    quadraticTo(
                        slantedX(x_o_r, yOInnerBottom), yOInnerBottom,
                        slantedX(x_o_r - r_o, yOInnerBottom), yOInnerBottom
                    )
                    lineTo(slantedX(x_o_l + S_w + r_o, yOInnerBottom), yOInnerBottom)
                    quadraticTo(
                        slantedX(x_o_l + S_w, yOInnerBottom), yOInnerBottom,
                        slantedX(x_o_l + S_w, yOInnerBottom - r_o), yOInnerBottom - r_o
                    )
                    close()
                }
                drawPath(
                    path = pathOInnerRounded,
                    color = Color.Transparent,
                    style = Fill,
                    blendMode = BlendMode.Clear
                )
            }
            
            Spacer(modifier = Modifier.height((fontSize * 0.05f).dp))
            
            Text(
                text = "E N E R G Y",
                fontSize = (fontSize * 0.38f).sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = subColor,
                letterSpacing = (fontSize * 0.16f).sp,
                modifier = Modifier.padding(start = (fontSize * 0.08f).dp)
            )
        }
        
        if (showSlogan) {
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(primaryColor.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "MOVIENDO EL FUTURO",
                    fontSize = (fontSize * 0.45f).sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "SERVICIO TECNICO",
                    fontSize = (fontSize * 0.35f).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun AppHeader(
    isAdminMode: Boolean,
    onToggleMode: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title using our beautiful vector representation
            LitioBrandLogo(
                showSlogan = true,
                fontSize = 17f,
                primaryColor = MaterialTheme.colorScheme.primary,
                subColor = MaterialTheme.colorScheme.onSurface
            )

            // Mode Selector
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onToggleMode() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("mode_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = "Mode Icon",
                        tint = if (isAdminMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAdminMode) "Técnico/Admin ⚡" else "Cliente 👤",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAdminMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ClientWelcomeScreen(viewModel: LitioViewModel) {
    var isRegisterTab by remember { mutableStateOf(false) }
    var welcomeQueryMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("client_welcome_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Image Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_brand),
                        contentDescription = "Showroom Litio Energy",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ESPECIALISTAS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Taller de Movilidad Eléctrica",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Tab Selectors
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (!isRegisterTab) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else Color.Transparent
                        )
                        .clickable { isRegisterTab = false }
                        .padding(vertical = 12.dp)
                        .testTag("tab_lookup"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (!isRegisterTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CONSULTAR AVANCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isRegisterTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isRegisterTab) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else Color.Transparent
                        )
                        .clickable { isRegisterTab = true }
                        .padding(vertical = 12.dp)
                        .testTag("tab_register"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Register",
                            tint = if (isRegisterTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INGRESAR EQUIPO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRegisterTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Subform Display
        item {
            if (!isRegisterTab) {
                LookupStatusCard(viewModel = viewModel)
            } else {
                RegistrationCard(viewModel = viewModel)
            }
        }

        if (!isRegisterTab) {
            // Educational Chatbot Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Infinite floating/bobbing and wobble animation for LitioBot mascot
                            val infiniteTransition = rememberInfiniteTransition(label = "bot_header_anim")
                            val floatAnim by infiniteTransition.animateFloat(
                                initialValue = -3f,
                                targetValue = 3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "float"
                            )
                            val rotateAnim by infiniteTransition.animateFloat(
                                initialValue = -6f,
                                targetValue = 6f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "rotate"
                            )

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .graphicsLayer(
                                        translationY = floatAnim,
                                        rotationZ = rotateAnim
                                    )
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_litiobot),
                                    contentDescription = "LitioBot",
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "LitioBot - Soporte y Diagnóstico ⚡",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Aprende sobre errores y fallas de tu vehículo",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        ChatWindow(viewModel = viewModel)
                    }
                }
            }
        }

        // Sedes y Contacto Directo Card / Publicidad de Movilidad Gratis
        item {
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
            if (!isRegisterTab) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Sedes",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Nuestras Sedes - Litio Energy 📍",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Especialistas en Movilidad Eléctrica",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid of Branches with Google Maps search URLs
                        val branches = listOf(
                            Triple("SURCO", "Av. Santiago de Surco 4352", "https://www.google.com/maps/search/?api=1&query=Av.+Santiago+de+Surco+4352,+Santiago+de+Surco"),
                            Triple("SAN BORJA", "Av. Aviación 2410 Stand 17", "https://www.google.com/maps/search/?api=1&query=Av.+Aviacion+2410,+San+Borja"),
                            Triple("LINCE", "Av. José Leal 571", "https://www.google.com/maps/search/?api=1&query=Av.+Jose+Leal+571,+Lince"),
                            Triple("SAN ISIDRO", "Av. Arenales 2584", "https://www.google.com/maps/search/?api=1&query=Av.+Arenales+2584,+San+Isidro")
                        )

                        branches.forEach { (name, address, mapsUrl) ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        try {
                                            uriHandler.openUri(mapsUrl)
                                        } catch (e: Exception) {
                                            // Ignore or fallback
                                        }
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = "📍",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Ver mapa ➔",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        )
                                    }
                                    Text(
                                        text = address,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = welcomeQueryMessage,
                            onValueChange = { welcomeQueryMessage = it },
                            label = { Text("Escribe tu consulta o duda sobre tu scooter...", fontSize = 12.sp) },
                            placeholder = { Text("Ej: ¿Cuánto cuesta el cambio de batería de litio?", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Message, null, modifier = Modifier.size(18.dp), tint = Color(0xFF25D366)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF25D366),
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                focusedLabelColor = Color(0xFF25D366)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contact button
                        Button(
                            onClick = {
                                try {
                                    val baseText = if (welcomeQueryMessage.isNotBlank()) {
                                        "¡Hola Litio Energy! Tengo la siguiente consulta sobre mi scooter: $welcomeQueryMessage"
                                    } else {
                                        "¡Hola Litio Energy! Quisiera hacer una consulta técnica sobre mi vehículo eléctrico."
                                    }
                                    val encodedText = java.net.URLEncoder.encode(baseText, "UTF-8")
                                    uriHandler.openUri("https://api.whatsapp.com/send?phone=51975925094&text=$encodedText")
                                } catch (e: Exception) {
                                    uriHandler.openUri("https://api.whatsapp.com/send?phone=51975925094")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "WhatsApp",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Contactar por WhatsApp (975 925 094)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                PeugeotVanAdCard(onOpenUrl = { url ->
                    try {
                        uriHandler.openUri(url)
                    } catch (e: Exception) {
                        // Fallback
                    }
                })
            }
        }
    }
}

@Composable
fun LookupStatusCard(viewModel: LitioViewModel) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Seguimiento en Tiempo Real",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ingresa tu número de DNI/C.E. que registraste al dejar tu equipo para ver los detalles de tu servicio técnico.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.lookupDniQuery,
                onValueChange = { 
                    viewModel.lookupDniQuery = it.filter { char -> char.isLetterOrDigit() } 
                },
                label = { Text("DNI/C.E.") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.QrCode, 
                        contentDescription = "Documento de Identidad",
                        tint = MaterialTheme.colorScheme.primary
                    ) 
                },
                placeholder = { Text("Ej: 12345678 o 001234567") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { viewModel.lookupClientByDni() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lookup_dni_input")
            )

            if (viewModel.lookupError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = viewModel.lookupError ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.lookupClientByDni() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("lookup_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = "Consultar",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Acceder y Ver Estado", 
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Club Litio Energy Advertisement Card
            ClubLitioInteractiveCard(onOpenUrl = { url ->
                try {
                    uriHandler.openUri(url)
                } catch (e: Exception) {
                    // Fallback
                }
            })
        }
    }
}

@Composable
fun RegistrationCard(viewModel: LitioViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Registrar Nueva Reparación",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ingresa tus datos personales y las especificaciones del equipo para crear tu ficha técnica interactiva.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // VEHICLE TYPE SELECTOR
            Text(
                text = "Tipo de Vehículo Eléctrico:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf(
                    Triple("Scooter", "Scooter 🛴", Icons.Default.ElectricScooter),
                    Triple("Bicicleta", "Bici 🚲", Icons.Default.DirectionsBike),
                    Triple("Moto", "Moto 🏍️", Icons.Default.ElectricMoped),
                    Triple("Otros", "Otros 🔧", Icons.Default.Build)
                )
                types.forEach { (typeKey, label, icon) ->
                    val isSelected = viewModel.regVehicleType == typeKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.regVehicleType = typeKey }
                            .padding(vertical = 10.dp)
                            .testTag("reg_type_$typeKey"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inputs
            OutlinedTextField(
                value = viewModel.regName,
                onValueChange = { viewModel.regName = it },
                label = { Text("Nombre Completo *") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_name_input")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.regPhone,
                onValueChange = { viewModel.regPhone = it.filter { char -> char.isDigit() || char == '+' } },
                label = { Text("Número Celular *") },
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_phone_input")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.regDni,
                onValueChange = { viewModel.regDni = it.filter { char -> char.isLetterOrDigit() } },
                label = { Text("DNI/C.E.") },
                leadingIcon = { Icon(Icons.Default.QrCode, null, tint = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_dni_input")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.regEmail,
                onValueChange = { viewModel.regEmail = it },
                label = { Text("Email (Opcional)") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Detalles del Vehículo:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = viewModel.regVehicleBrand,
                    onValueChange = { viewModel.regVehicleBrand = it },
                    label = { Text("Marca *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_brand_input")
                )
                OutlinedTextField(
                    value = viewModel.regVehicleModel,
                    onValueChange = { viewModel.regVehicleModel = it },
                    label = { Text("Modelo *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_model_input")
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.regVehicleSerial,
                onValueChange = { viewModel.regVehicleSerial = it },
                label = { Text("Nº Serie / Otros") },
                placeholder = { Text("Escribe Nº Serie, 'Otros' o detalles") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.regProblem,
                onValueChange = { viewModel.regProblem = it },
                label = { Text("Falla Reportada o Descripción") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lugar de Ingreso (Sede) *",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val sedes = listOf("Litio Surco", "Litio San Borja", "Litio San Isidro", "Litio Lince")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    sedes.take(2).forEach { sedeName ->
                        val isSelected = viewModel.regSede == sedeName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.regSede = sedeName }
                                .padding(vertical = 12.dp)
                                .testTag("reg_sede_${sedeName.replace(" ", "_").lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sedeName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    sedes.takeLast(2).forEach { sedeName ->
                        val isSelected = viewModel.regSede == sedeName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.regSede = sedeName }
                                .padding(vertical = 12.dp)
                                .testTag("reg_sede_${sedeName.replace(" ", "_").lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sedeName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val isFormValid = viewModel.regName.isNotBlank() && 
                    viewModel.regPhone.isNotBlank() && 
                    viewModel.regVehicleBrand.isNotBlank() && 
                    viewModel.regVehicleModel.isNotBlank()

            Button(
                onClick = { viewModel.registerClient() },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("register_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Icon(Icons.Default.Add, null, tint = if (isFormValid) Color.Black else Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Registrar e Ingresar",
                    fontWeight = FontWeight.Bold,
                    color = if (isFormValid) Color.Black else Color.Gray
                )
            }
        }
    }
}

@Composable
fun ClientDashboard(
    viewModel: LitioViewModel,
    onOpenUrl: (String) -> Unit
) {
    val client = viewModel.trackedClient ?: return
    val listState = rememberLazyListState()
    var customRepairQuery by remember { mutableStateOf("") }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("client_dashboard")
            .imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Logout Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bienvenido, ${client.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Ficha de Servicio Nº ${client.id}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                OutlinedButton(
                    onClick = { viewModel.logoutClient() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Repair Progress Visualizer Card
        item {
            ServiceProgressCard(client = client)
        }

        // Details Grid (Cost, Delivery, Serial)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cost Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Payments, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Costo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("S/. ${String.format("%.2f", client.estimatedCost)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                // Delivery Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Entrega Est.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(client.estimatedCompletionDate, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        // Details of vehicle
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Especificaciones Técnicas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            TechnicalDetailRow("Marca/Modelo:", "${client.vehicleBrand} ${client.vehicleModel}")
                            TechnicalDetailRow("Tipo de Equipo:", client.vehicleType)
                            TechnicalDetailRow("Nº de Serie:", client.vehicleSerialNumber)
                            TechnicalDetailRow("DNI/C.E.:", client.dni.ifBlank { "No registrado" })
                            TechnicalDetailRow("Lugar de Ingreso:", client.sede)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            TechnicalDetailRow("Falla Inicial:", client.problemDescription)
                        }
                    }

                    if (client.technicianNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Notes",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Informe del Especialista Litio Energy:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = client.technicianNotes,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp Business Quick Contact Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF075E54).copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WhatsApp Business Técnico ⚡",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF25D366)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Envía tu consulta técnica sobre el avance de tu scooter directamente al WhatsApp de Litio Energy.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = customRepairQuery,
                        onValueChange = { customRepairQuery = it },
                        label = { Text("Escribe tu duda o consulta sobre tu scooter...", fontSize = 12.sp) },
                        placeholder = { Text("Ej: ¿Tienen listo el diagnóstico del motor?", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Message, null, modifier = Modifier.size(18.dp), tint = Color(0xFF25D366)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF25D366),
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFF25D366)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { onOpenUrl(viewModel.getWhatsAppIntentUrl(customRepairQuery)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("whatsapp_button")
                    ) {
                        Icon(Icons.Default.Share, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enviar consulta por WhatsApp", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceProgressCard(client: ClientEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Heading with status icon
            val (statusColor, icon) = when (client.status) {
                "Recibido" -> Pair(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), Icons.Default.Info)
                "Diagnóstico" -> Pair(MaterialTheme.colorScheme.primary, Icons.Default.Search)
                "En Reparación" -> Pair(MaterialTheme.colorScheme.tertiary, Icons.Default.Build)
                "Control de Calidad" -> Pair(MaterialTheme.colorScheme.secondary, Icons.Default.Message)
                "Listo para Retirar" -> Pair(MaterialTheme.colorScheme.secondary, Icons.Default.CheckCircle)
                else -> Pair(MaterialTheme.colorScheme.primary, Icons.Default.Info)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(statusColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = statusColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("ESTADO DE AVANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = client.status.uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor
                        )
                    }
                }

                // Percent Text
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${client.progress}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Progress Bar
            val animatedProgress by animateFloatAsState(targetValue = client.progress / 100f)
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Horizontal Stepper Visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val stages = listOf(
                    Triple("Recibido", "📥\nRecibido", 10),
                    Triple("Diagnóstico", "🔍\nDiag.", 25),
                    Triple("En Reparación", "🔧\nRepar.", 60),
                    Triple("Control de Calidad", "🧪\nCalid.", 85),
                    Triple("Listo para Retirar", "✅\nListo", 100)
                )

                stages.forEach { (stageName, label, minProgress) ->
                    val isActive = client.progress >= minProgress
                    val isCurrent = client.status.equals(stageName, ignoreCase = true)
                    
                    val nodeColor = when {
                        isCurrent -> statusColor
                        isActive -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                    }
                    
                    val textColor = when {
                        isCurrent -> statusColor
                        isActive -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    if (isCurrent) nodeColor.copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                )
                                .border(
                                    width = if (isCurrent) 3.dp else 2.dp,
                                    color = nodeColor,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            fontSize = 8.sp,
                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicalDetailRow(label: String, valText: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = valText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChatWindow(viewModel: LitioViewModel) {
    val coroutineScope = remember { kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main) }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(viewModel.chatMessages.size) {
        if (viewModel.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.chatMessages.size - 1)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Messages Panel
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.chatMessages) { message ->
                    ChatBubble(message = message)
                }
                
                if (viewModel.isChatLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LitioBot está escribiendo...",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.chatInputText,
                    onValueChange = { viewModel.chatInputText = it },
                    placeholder = { Text("Escribe tu consulta o dudas de tu vehículo eléctrico...", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { viewModel.sendChatMessage() }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .testTag("chat_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    enabled = viewModel.chatInputText.isNotBlank() && !viewModel.isChatLoading,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (viewModel.chatInputText.isNotBlank() && !viewModel.isChatLoading) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = CircleShape
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = if (viewModel.chatInputText.isNotBlank() && !viewModel.isChatLoading) Color.Black else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                      else MaterialTheme.colorScheme.surfaceVariant
    val shape = if (message.isUser) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 0.dp)
    } else {
        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 12.dp)
    }
    
    val bubbleBorder = if (message.isUser) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Surface(
            color = bubbleColor,
            border = bubbleBorder,
            shape = shape,
            tonalElevation = if (message.isUser) 0.dp else 1.dp,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                if (!message.isUser) {
                    Text(
                        text = "LitioBot ⚡",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun AdminDashboard(viewModel: LitioViewModel) {
    val clients by viewModel.clientsList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Stats Panel
            AdminStatsPanel(clients = clients)

            // Configuration for WhatsApp
            AdminWhatsappConfigRow(
                whatsappNumber = viewModel.whatsappNumber,
                onNumberChanged = { viewModel.updateWhatsappNumber(it) }
            )

            // Configuration for Google Sheets
            AdminGoogleSheetsConfigRow(
                webhookUrl = viewModel.googleSheetsWebhookUrl,
                onWebhookUrlChanged = { viewModel.updateGoogleSheetsWebhookUrl(it) },
                syncStatus = viewModel.syncStatusMessage,
                onClearStatus = { viewModel.syncStatusMessage = null },
                onSyncAll = { viewModel.syncAllToGoogleSheets(clients) },
                isSyncing = viewModel.isSyncingSheets,
                onResetUrl = { viewModel.resetToDefaultGoogleSheetsUrl() }
            )

            // Filters Box
            AdminFiltersBox(
                searchQuery = viewModel.adminSearchQuery,
                selectedFilter = viewModel.adminStatusFilter,
                onFilterChanged = { q, f -> viewModel.updateAdminFilters(q, f) }
            )

            // Clients List
            if (clients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No se encontraron clientes registrados.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(clients) { client ->
                        AdminClientCard(
                            client = client,
                            onEdit = {
                                viewModel.clientBeingEdited = client
                                viewModel.isEditingClientDialogVisible = true
                            },
                            onDelete = { viewModel.deleteClient(client) },
                            onSync = { viewModel.syncToGoogleSheets(client) }
                        )
                    }
                }
            }
        }

        // FAB to add new client
        FloatingActionButton(
            onClick = { viewModel.isAddingClientDialogVisible = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .navigationBarsPadding()
                .testTag("add_client_fab")
        ) {
            Icon(Icons.Default.Add, "Agregar Cliente")
        }

        // Dialogs
        if (viewModel.isEditingClientDialogVisible && viewModel.clientBeingEdited != null) {
            EditClientDialog(
                client = viewModel.clientBeingEdited!!,
                onDismiss = { 
                    viewModel.isEditingClientDialogVisible = false
                    viewModel.clientBeingEdited = null
                },
                onSave = { updatedClient ->
                    viewModel.saveClientChanges(updatedClient)
                    viewModel.isEditingClientDialogVisible = false
                    viewModel.clientBeingEdited = null
                }
            )
        }

        if (viewModel.isAddingClientDialogVisible) {
            AddClientDialog(
                onDismiss = { viewModel.isAddingClientDialogVisible = false },
                onAdd = { newClient ->
                    viewModel.addNewClient(newClient)
                    viewModel.isAddingClientDialogVisible = false
                }
            )
        }
    }
}

@Composable
fun AdminStatsPanel(clients: List<ClientEntity>) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val total = clients.size
            val ready = clients.count { it.status == "Listo para Retirar" }
            val inRepair = clients.count { it.status == "En Reparación" }
            val diagnos = clients.count { it.status == "Diagnóstico" || it.status == "Recibido" }

            StatCard(title = "Total Equipos", value = "$total", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            StatCard(title = "En Reparación", value = "$inRepair", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
            StatCard(title = "Listos ✅", value = "$ready", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun AdminWhatsappConfigRow(
    whatsappNumber: String,
    onNumberChanged: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Config",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "WhatsApp Business:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = whatsappNumber,
                onValueChange = { onNumberChanged(it) },
                singleLine = true,
                placeholder = { Text("Ej: +51987654321") },
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("admin_whatsapp_input")
            )
        }
    }
}

@Composable
fun AdminGoogleSheetsConfigRow(
    webhookUrl: String,
    onWebhookUrlChanged: (String) -> Unit,
    syncStatus: String?,
    onClearStatus: () -> Unit,
    onSyncAll: () -> Unit,
    isSyncing: Boolean,
    onResetUrl: () -> Unit
) {
    var showGuide by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Sheets Sync",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google Sheets Webhook:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = webhookUrl,
                    onValueChange = { onWebhookUrlChanged(it) },
                    singleLine = true,
                    placeholder = { Text("Pega la URL de Apps Script") },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("admin_sheets_input")
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { showGuide = !showGuide },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (showGuide) Icons.Default.Help else Icons.Default.HelpOutline,
                        contentDescription = "Ver Guía",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sync All Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset to default button
                TextButton(
                    onClick = onResetUrl,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restablecer URL",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restablecer Webhook", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                }

                Button(
                    onClick = onSyncAll,
                    enabled = !isSyncing && webhookUrl.isNotBlank(),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(28.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            strokeWidth = 1.5.dp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sincronizando...", fontSize = 10.sp, color = Color.Black)
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sincronizar todos a Google Sheets", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            // Sync Status Banner
            if (!syncStatus.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (syncStatus.contains("Error") || syncStatus.contains("❌")) 
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            else 
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = syncStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (syncStatus.contains("Error") || syncStatus.contains("❌")) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(12.dp)
                            .clickable { onClearStatus() }
                    )
                }
            }

            // Collapsible setup guide
            if (showGuide) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "⚡ GUÍA DE ENLACE DE GOOGLE SHEETS ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. Abre tu hoja de Google Sheets.\n" +
                                   "2. Haz clic en 'Extensiones' > 'Apps Script'.\n" +
                                   "3. Borra el código existente y pega el script de abajo.\n" +
                                   "4. Presiona 'Implementar' > 'Nueva implementación'.\n" +
                                   "5. Elige tipo 'Aplicación web'. Configura 'Quién tiene acceso' como 'Cualquiera' (Everyone).\n" +
                                   "6. Copia la URL de la aplicación web y pégala arriba.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Script Container for copy paste
                        Text(
                            text = "CÓDIGO APPS SCRIPT A PEGAR:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "function doPost(e) {\n" +
                                       "  try {\n" +
                                       "    var d = JSON.parse(e.postData.contents);\n" +
                                       "    var sheetName = d.sheetName || 'Clientes litio energy';\n" +
                                       "    var ss = SpreadsheetApp.getActiveSpreadsheet();\n" +
                                       "    var s = ss.getSheetByName(sheetName);\n" +
                                       "    if (!s) {\n" +
                                       "      s = ss.insertSheet(sheetName);\n" +
                                       "    }\n" +
                                       "    if (s.getLastRow() === 0) {\n" +
                                       "      s.appendRow(['ID', 'Fecha', 'Nombre', 'DNI', 'Celular', 'Email', 'Tipo', 'Marca', 'Modelo', 'Nº Serie', 'Falla', 'Estado', 'Progreso', 'Costo', 'Fecha Entrega', 'Sede', 'Notas']);\n" +
                                       "    }\n" +
                                       "    s.appendRow([d.id, new Date().toLocaleString(), d.name, d.dni || '', d.phone, d.email, d.vehicleType, d.vehicleBrand, d.vehicleModel, d.vehicleSerialNumber, d.problemDescription, d.status, d.progress, d.estimatedCost, d.estimatedCompletionDate, d.sede || '', d.technicianNotes]);\n" +
                                       "    return ContentService.createTextOutput(JSON.stringify({status:'success', message:'Sincronizado en: ' + sheetName})).setMimeType(ContentService.MimeType.JSON);\n" +
                                       "  } catch(err) {\n" +
                                       "    return ContentService.createTextOutput(JSON.stringify({status:'error', message:err.toString()})).setMimeType(ContentService.MimeType.JSON);\n" +
                                       "  }\n" +
                                       "}",
                                fontSize = 8.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFiltersBox(
    searchQuery: String,
    selectedFilter: String,
    onFilterChanged: (String, String) -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onFilterChanged(it, selectedFilter) },
                placeholder = { Text("Buscar cliente, marca, serie...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp)) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("admin_search_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Filter button
            Box {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable { expandedDropdown = true }
                        .testTag("filter_dropdown_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    val states = listOf("Todos", "Recibido", "Diagnóstico", "En Reparación", "Control de Calidad", "Listo para Retirar")
                    states.forEach { state ->
                        val isSelected = selectedFilter == state
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = state, 
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                ) 
                            },
                            onClick = {
                                onFilterChanged(searchQuery, state)
                                expandedDropdown = false
                            },
                            modifier = Modifier.testTag("filter_option_$state")
                        )
                    }
                }
            }
        }
        
        if (selectedFilter != "Todos") {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Filtro: $selectedFilter",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(10.dp)
                                .clickable { onFilterChanged(searchQuery, "Todos") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminClientCard(
    client: ClientEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSync: () -> Unit
) {
    val (statusColor, vehicleIcon) = when (client.status) {
        "Recibido" -> Pair(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), Icons.Default.Info)
        "Diagnóstico" -> Pair(MaterialTheme.colorScheme.primary, Icons.Default.Search)
        "En Reparación" -> Pair(MaterialTheme.colorScheme.tertiary, Icons.Default.Build)
        "Control de Calidad" -> Pair(MaterialTheme.colorScheme.secondary, Icons.Default.Message)
        "Listo para Retirar" -> Pair(MaterialTheme.colorScheme.secondary, Icons.Default.CheckCircle)
        else -> Pair(MaterialTheme.colorScheme.primary, Icons.Default.Info)
    }

    val typeIcon = when (client.vehicleType) {
        "Scooter" -> Icons.Default.ElectricScooter
        "Bicicleta" -> Icons.Default.ElectricBike
        "Moto" -> Icons.Default.ElectricMoped
        else -> Icons.Default.TwoWheeler
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_client_card_${client.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(typeIcon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = client.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cel: ${client.phone} | DNI/C.E.: ${client.dni.ifBlank { "N/A" }} | S/N: ${client.vehicleSerialNumber}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Sync / Edit / Delete Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSync,
                        modifier = Modifier.size(32.dp).testTag("sync_client_button_${client.id}")
                    ) {
                        Icon(Icons.Default.CloudSync, "Sincronizar", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp).testTag("edit_client_button_${client.id}")
                    ) {
                        Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("delete_client_button_${client.id}")
                    ) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle Specs and Issue
            Text(
                text = "Equipo: ${client.vehicleType} ${client.vehicleBrand} ${client.vehicleModel}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Falla: ${client.problemDescription}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar and Cost
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    LinearProgressIndicator(
                        progress = { client.progress / 100f },
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${client.status} (${client.progress}%)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Text(
                    text = "S/. ${String.format("%.2f", client.estimatedCost)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, color)
}

@Composable
fun EditClientDialog(
    client: ClientEntity,
    onDismiss: () -> Unit,
    onSave: (ClientEntity) -> Unit
) {
    var status by remember { mutableStateOf(client.status) }
    var progress by remember { mutableStateOf(client.progress.toFloat()) }
    var notes by remember { mutableStateOf(client.technicianNotes) }
    var costText by remember { mutableStateOf(client.estimatedCost.toString()) }
    var deliveryDate by remember { mutableStateOf(client.estimatedCompletionDate) }

    val states = listOf("Recibido", "Diagnóstico", "En Reparación", "Control de Calidad", "Listo para Retirar")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .testTag("edit_client_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Actualizar Servicio", fontSize = 16.dp.value.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    Text("Cliente: ${client.name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Status Dropdown selector
                item {
                    Text("Estado de Reparación:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        states.forEach { stateName ->
                            val isSelected = status == stateName
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        status = stateName 
                                        // Auto-set standard progress based on selected status
                                        progress = when (stateName) {
                                            "Recibido" -> 10f
                                            "Diagnóstico" -> 25f
                                            "En Reparación" -> 60f
                                            "Control de Calidad" -> 85f
                                            "Listo para Retirar" -> 100f
                                            else -> progress
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .border(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
                                        .padding(3.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stateName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                // Progress Slider
                item {
                    Text("Progreso: ${progress.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("progress_slider")
                    )
                }

                // Cost and completion
                item {
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        label = { Text("Costo del Servicio (S/.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_cost_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = deliveryDate,
                        onValueChange = { deliveryDate = it },
                        label = { Text("Fecha Estimada de Entrega") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_delivery_input")
                    )
                }

                // Tech Notes
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Informe Técnico / Comentarios") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth().testTag("edit_notes_input")
                    )
                }

                // Save buttons
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val costVal = costText.toDoubleOrNull() ?: client.estimatedCost
                            val updated = client.copy(
                                status = status,
                                progress = progress.toInt(),
                                technicianNotes = notes,
                                estimatedCost = costVal,
                                estimatedCompletionDate = deliveryDate
                            )
                            onSave(updated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_client_changes_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Guardar Cambios", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddClientDialog(
    onDismiss: () -> Unit,
    onAdd: (ClientEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var vType by remember { mutableStateOf("Scooter") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("0.00") }
    var sede by remember { mutableStateOf("Litio Surco") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .testTag("add_client_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Registrar Nuevo Cliente", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_name_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.filter { char -> char.isDigit() || char == '+' } },
                        label = { Text("Número Celular *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("add_phone_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = dni,
                        onValueChange = { dni = it.filter { char -> char.isLetterOrDigit() } },
                        label = { Text("DNI/C.E.") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth().testTag("add_dni_input")
                    )
                }

                item {
                    // Type selector
                    Text("Tipo de Vehículo:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf("Scooter", "Bicicleta", "Moto", "Otros")
                        types.forEach { type ->
                            val isSelected = vType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, RoundedCornerShape(6.dp))
                                    .clickable { vType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(type, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Marca *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("add_brand_input")
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Modelo *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("add_model_input")
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = serial,
                        onValueChange = { serial = it },
                        label = { Text("Nº Serie / Otros") },
                        placeholder = { Text("Escribe Nº Serie, 'Otros' o detalles") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = problem,
                        onValueChange = { problem = it },
                        label = { Text("Falla / Diagnóstico Inicial") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Sede selector
                    Text("Lugar de Ingreso (Sede):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sedes = listOf("Litio Surco", "Litio San Borja", "Litio San Isidro", "Litio Lince")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sedes.take(2).forEach { sedeName ->
                                val isSelected = sede == sedeName
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, RoundedCornerShape(6.dp))
                                        .clickable { sede = sedeName }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(sedeName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sedes.takeLast(2).forEach { sedeName ->
                                val isSelected = sede == sedeName
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, RoundedCornerShape(6.dp))
                                        .clickable { sede = sedeName }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(sedeName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Costo Inicial (S/.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    val isFormValid = name.isNotBlank() && phone.isNotBlank() && brand.isNotBlank() && model.isNotBlank()
                    Button(
                        onClick = {
                            val newC = ClientEntity(
                                name = name,
                                phone = phone,
                                dni = dni,
                                email = email.ifBlank { "no-email@litio.com" },
                                vehicleType = vType,
                                vehicleBrand = brand,
                                vehicleModel = model,
                                vehicleSerialNumber = serial.ifBlank { "Otros" },
                                problemDescription = problem.ifBlank { "Revisión preventiva" },
                                status = "Recibido",
                                progress = 10,
                                technicianNotes = "Ingresado a taller.",
                                estimatedCost = cost.toDoubleOrNull() ?: 0.0,
                                estimatedCompletionDate = "A coordinar",
                                sede = sede
                            )
                            onAdd(newC)
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_add_client_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Ingresar Vehículo", fontWeight = FontWeight.Bold, color = if (isFormValid) Color.Black else Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun PeugeotVanAdCard(
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Header with White Service Camioneta Image - Larger and Bolder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.camioneta_litio_energy_1783563676419),
                    contentDescription = "Camioneta de Servicio Litio Energy",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                
                // Active Promotion Badge - Redesigned to stand out
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "MOVILIDAD 100% GRATIS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                // Litio Energy miniature logo
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LitioBrandLogo(
                        fontSize = 14f,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        subColor = Color.White
                    )
                }
            }

            // Description and Interaction - Spaced out and bolder
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "¡Recogemos tu vehículo gratis! Solo envíanos tu ubicación 🚚",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "En Litio Energy te lo ponemos fácil: nuestra camioneta de servicio se desplazará a tu domicilio o donde te encuentres para recoger tu scooter, bicicleta o moto eléctrica sin costo adicional. ¡Olvídate de empujar o cargar tu vehículo!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(18.dp))
                
                Button(
                    onClick = {
                        try {
                            val baseText = "¡Hola Litio Energy! Deseo coordinar el recojo gratuito de mi vehículo eléctrico con su camioneta de servicio. Adjunto mi ubicación actual:"
                            val encodedText = java.net.URLEncoder.encode(baseText, "UTF-8")
                            onOpenUrl("https://api.whatsapp.com/send?phone=51975925094&text=$encodedText")
                        } catch (e: Exception) {
                            onOpenUrl("https://api.whatsapp.com/send?phone=51975925094")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366) // WhatsApp branding color
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Enviar Ubicación",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enviar Ubicación por WhatsApp",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ClubLitioInteractiveCard(
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnnual by remember { mutableStateOf(false) }
    var expandedBenefit by remember { mutableStateOf<Int?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "lightning_transition")
    
    // Scale animation synchronized with the strike keyframes
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2400
                0.5f at 0
                0.5f at 1100
                1.4f at 1200 // strike scale-up
                1.0f at 1300
                0.8f at 1350
                1.3f at 1450 // bounce/double-flash scale
                1.0f at 1550
                0.5f at 1800
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "lightning_scale"
    )
    
    // Rotation animation synchronized with the strike keyframes
    val rotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2400
                0f at 0
                0f at 1100
                -15f at 1200
                15f at 1300
                -5f at 1450
                5f at 1550
                0f at 1700
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "lightning_rotation"
    )

    // Opacity/Alpha animation simulating the flashing of real lightning
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2400
                0.0f at 0
                0.0f at 1100
                1.0f at 1200 // strike! maximum light
                0.1f at 1350 // quick off
                1.0f at 1450 // secondary flash
                0.0f at 1700 // fade to nothing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "lightning_alpha"
    )

    // Falling / translationY animation simulating the lightning striking from above
    val translationY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2400
                -15f at 0
                -15f at 1100 // up and waiting
                0f at 1200   // strikes down instantly!
                0f at 2400   // remains down
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "lightning_translation"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with premium VIP Crown badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .border(
                            width = 0.8.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👑",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 1.dp) // Visual centering adjustment
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Club LITIO VIP",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary, // Fits perfectly with the app's palette
                            letterSpacing = 0.3.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "⚡",
                            fontSize = 18.sp,
                            modifier = Modifier.graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation,
                                alpha = alpha,
                                translationY = translationY
                            )
                        )
                    }
                    Text(
                        text = "Membresía Premium Todo Incluido",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "¡Ahorra dinero y mantén tu vehículo al 100%! Únete a nuestro selecto club con beneficios inigualables.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Plan Toggle Switch (Mensual vs Anual)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mensual Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isAnnual) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { isAnnual = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Plan Mensual",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isAnnual) Color.Black else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Anual Button with badge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isAnnual) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { isAnnual = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Plan Anual",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAnnual) Color.Black else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isAnnual) Color.Black else Color(0xFFFF5252),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Ahorra 20%",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isAnnual) MaterialTheme.colorScheme.primary else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pricing Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isAnnual) "S/ 40.00" else "S/ 50.00",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " / mes",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAnnual) "Cobrado anualmente (S/ 480.00 al año - ¡Socio VIP!)" else "Pago mensual sin contratos de permanencia",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAnnual) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Benefits Header
            Text(
                text = "Beneficios incluidos (Toca para ver detalles) 👇",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Benefits Grid / Accordion
            val benefits = listOf(
                BenefitItemData(
                    id = 1,
                    icon = "🛠️",
                    title = "Mantenimientos Preventivos y Generales GRATIS",
                    desc = "¡Ilimitados durante el año! Olvídate de pagar por cada ajuste. Revisión de batería, frenos, electrónica, electromecánica y motor, ¡todo gratis!"
                ),
                BenefitItemData(
                    id = 2,
                    icon = "🏷️",
                    title = "Descuentos en todo tipo de repuestos",
                    desc = "Ahorra en todo tipo de llantas ULIP, pastillas, zapatas, aceleradores, controladoras, pantallas, manijas de frenos, armado de baterías, etc... con precios exclusivos por ser socio del CLUB LITIO VIP."
                ),
                BenefitItemData(
                    id = 3,
                    icon = "🚚",
                    title = "Movilidad de recogida GRATIS",
                    desc = "Si tu equipo se queda sin carga o falla en la calle, nuestra camioneta de servicio va a recogerlo a domicilio 100% gratis."
                ),
                BenefitItemData(
                    id = 4,
                    icon = "💬",
                    title = "Prioridad VIP en Taller y Chat",
                    desc = "Tu scooter salta la fila. Reparación prioritaria garantizada y asesoría técnica 24/7 personalizada."
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                benefits.forEach { benefit ->
                    val isExpanded = expandedBenefit == benefit.id
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            }
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isExpanded) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedBenefit = if (isExpanded) null else benefit.id
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(benefit.icon, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = benefit.title,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                                Text(
                                    text = if (isExpanded) "▲" else "▼",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = benefit.desc,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(start = 26.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Join Button (WhatsApp CTA)
            Button(
                onClick = {
                    try {
                        val planName = if (isAnnual) "Plan Anual (S/ 40.00/mes)" else "Plan Mensual (S/ 50.00/mes)"
                        val baseText = "¡Hola Litio Energy! Estoy sumamente interesado en unirme al CLUB LITIO VIP en el $planName para disfrutar de Mantenimientos Preventivos y Generales Gratis, Descuentos en Repuestos y Movilidad Gratis. ¿Cómo puedo iniciar mi suscripción?"
                        val encodedText = java.net.URLEncoder.encode(baseText, "UTF-8")
                        onOpenUrl("https://api.whatsapp.com/send?phone=51975925094&text=$encodedText")
                    } catch (e: Exception) {
                        onOpenUrl("https://api.whatsapp.com/send?phone=51975925094")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366) // WhatsApp color
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unirse",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAnnual) "Unirse al Club - Plan Anual" else "Unirse al Club - Plan Mensual",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class BenefitItemData(
    val id: Int,
    val icon: String,
    val title: String,
    val desc: String
)

