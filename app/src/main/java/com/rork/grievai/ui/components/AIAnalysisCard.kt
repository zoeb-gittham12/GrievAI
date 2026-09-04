package com.rork.grievai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.AIAnalysis
import com.rork.grievai.data.ComplaintPriority
import com.rork.grievai.ui.theme.AIGradient

/**
 * Animated AI analysis card. Reveals category, priority and confidence
 * with a staggered entrance and a shimmering "AI" sheen.
 */
@Composable
fun AIAnalysisCard(
    analysis: AIAnalysis,
    modifier: Modifier = Modifier,
    animateOnShow: Boolean = true
) {
    var visible by remember { mutableStateOf(!animateOnShow) }
    LaunchedEffect(Unit) {
        if (animateOnShow) {
            kotlinx.coroutines.delay(150)
            visible = true
        }
    }

    val transition = rememberInfiniteTransition(label = "aiShimmer")
    val shimmer by transition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3A1D7A),
                        Color(0xFF1A237E),
                        Color(0xFF0D47A1)
                    )
                )
            )
            .padding(18.dp)
            .graphicsLayer { alpha = if (visible) 1f else 0f }
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AIGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("GrievAI Analysis", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("AI-powered complaint intelligence", color = Color.White.copy(alpha = 0.65f), style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.weight(1f))
            // shimmering dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.3f + 0.5f * (shimmer - shimmer.toInt())))
            )
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 },
            exit = fadeOut()
        ) {
            Column {
                AIInsightRow(
                    icon = Icons.Filled.Category,
                    label = "Category",
                    value = analysis.category,
                    tint = Color(0xFFB388FF)
                )
                Spacer(Modifier.height(10.dp))
                AIInsightRow(
                    icon = Icons.Filled.Psychology,
                    label = "Priority",
                    value = analysis.priority.label,
                    tint = when (analysis.priority) {
                        ComplaintPriority.LOW -> Color(0xFF81C784)
                        ComplaintPriority.MEDIUM -> Color(0xFF64B5F6)
                        ComplaintPriority.HIGH -> Color(0xFFFFB74D)
                        ComplaintPriority.CRITICAL -> Color(0xFFEF5350)
                    }
                )
                Spacer(Modifier.height(10.dp))
                AIInsightRow(
                    icon = Icons.Filled.AutoAwesome,
                    label = "Confidence Score",
                    value = "${(analysis.confidenceScore * 100).toInt()}%",
                    tint = Color(0xFFFFD54F)
                )
                Spacer(Modifier.height(8.dp))
                ConfidenceBar(analysis.confidenceScore)

                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(12.dp)
                ) {
                    Row {
                        Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("AI Insight", color = Color(0xFFFFD54F), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(analysis.insights, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AIInsightRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}
