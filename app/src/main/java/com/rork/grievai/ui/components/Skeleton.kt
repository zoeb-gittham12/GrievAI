package com.rork.grievai.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rork.grievai.ui.theme.LocalIsDark

/**
 * Shimmer-based skeleton loader for lists and cards.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 12
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    val base = if (LocalIsDark.current) Color(0xFF2A3146) else Color(0xFFE2E6F0)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(base.copy(alpha = alpha))
    )
}

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(180.dp), cornerRadius = 20)
        Spacer(Modifier.height(12.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f).height(18.dp))
        Spacer(Modifier.height(8.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.9f).height(14.dp))
        Spacer(Modifier.height(8.dp))
        Row {
            ShimmerBox(modifier = Modifier.width(80.dp).height(28.dp), cornerRadius = 14)
            Spacer(Modifier.width(8.dp))
            ShimmerBox(modifier = Modifier.width(80.dp).height(28.dp), cornerRadius = 14)
        }
    }
}

@Composable
fun SkeletonStatRow() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        ShimmerBox(modifier = Modifier.weight(1f).height(110.dp), cornerRadius = 18)
        Spacer(Modifier.width(12.dp))
        ShimmerBox(modifier = Modifier.weight(1f).height(110.dp), cornerRadius = 18)
    }
}

@Composable
fun SkeletonList(itemCount: Int = 4) {
    Column {
        repeat(itemCount) {
            SkeletonCard()
        }
    }
}
