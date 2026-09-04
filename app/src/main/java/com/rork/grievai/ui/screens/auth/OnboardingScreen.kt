package com.rork.grievai.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rork.grievai.ui.theme.Amber400
import com.rork.grievai.ui.theme.Amber600
import com.rork.grievai.ui.theme.Blue400
import com.rork.grievai.ui.theme.Blue500
import com.rork.grievai.ui.theme.HeroGradient
import com.rork.grievai.ui.theme.Indigo500
import com.rork.grievai.ui.theme.Indigo700

private data class OnboardSlide(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val description: String,
    val gradient: Brush
)

private val slides = listOf(
    OnboardSlide(
        Icons.Filled.Shield,
        "Voice Your Concerns",
        "Submit grievances securely",
        "Report any issue — from infrastructure to academics — in seconds. Your identity stays protected.",
        Brush.linearGradient(listOf(Indigo700, Blue500))
    ),
    OnboardSlide(
        Icons.Filled.AutoAwesome,
        "AI-Powered Intelligence",
        "Smart categorization & priority",
        "GrievAI automatically categorizes complaints, predicts priority, and routes them to the right department.",
        Brush.linearGradient(listOf(Indigo700, Amber600))
    ),
    OnboardSlide(
        Icons.Filled.TrendingUp,
        "Track & Resolve",
        "Transparent resolution timeline",
        "Follow every step from submission to resolution. Join public complaints to amplify your voice.",
        Brush.linearGradient(listOf(Blue500, Amber400))
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val slide = slides[currentPage]

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Decorative gradient header that morphs with page
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(slide.gradient)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            // Skip
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onFinish) {
                    Text("Skip", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Animated icon
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 3 },
                exit = fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 3 },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val iconSize by animateDpAsState(
                        targetValue = 120.dp,
                        animationSpec = tween(600),
                        label = "iconSize"
                    )
                    Box(
                        modifier = Modifier
                            .size(iconSize)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(slide.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Card with description
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Text(
                    text = slide.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(slides.size) { index ->
                    val selected = index == currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (selected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (currentPage < slides.lastIndex) currentPage++ else onFinish()
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (currentPage < slides.lastIndex) "Next" else "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
