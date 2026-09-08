package com.rork.grievai.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.grievai.ui.theme.HeroGradient
import kotlinx.coroutines.delay

@Composable
fun PinSetupScreen(
    onPinSet: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(0) } // 0 = enter, 1 = confirm
    var error by remember { mutableStateOf<String?>(null) }

    val current = if (stage == 0) pin else confirm

    fun onDigit(d: String) {
        error = null
        if (stage == 0) {
            if (pin.length < 4) pin += d
            if (pin.length == 4) stage = 1
        } else {
            if (confirm.length < 4) confirm += d
            if (confirm.length == 4) {
                if (confirm == pin) {
                    onPinSet(pin)
                } else {
                    error = "PINs do not match. Try again."
                    pin = ""
                    confirm = ""
                    stage = 0
                }
            }
        }
    }

    fun onBackspace() {
        error = null
        if (stage == 0) {
            if (pin.isNotEmpty()) pin = pin.dropLast(1)
        } else {
            if (confirm.isNotEmpty()) confirm = confirm.dropLast(1)
        }
    }

    PinPadLayout(
        title = if (stage == 0) "Create a 4-digit PIN" else "Confirm your PIN",
        subtitle = "Use this PIN to unlock GrievAI quickly next time.",
        filledCount = current.length,
        error = error,
        onDigit = ::onDigit,
        onBackspace = ::onBackspace
    )
}

@Composable
fun PinUnlockScreen(
    onUnlocked: () -> Unit,
    onUsePassword: () -> Unit,
    verifyPin: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            delay(120)
            if (verifyPin(pin)) {
                onUnlocked()
            } else {
                error = "Incorrect PIN"
                pin = ""
            }
        }
    }

    PinPadLayout(
        title = "Welcome back",
        subtitle = "Enter your 4-digit PIN",
        filledCount = pin.length,
        error = error,
        onDigit = {
            error = null
            if (pin.length < 4) pin += it
        },
        onBackspace = {
            error = null
            if (pin.isNotEmpty()) pin = pin.dropLast(1)
        },
        footer = {
            TextButton(onClick = onUsePassword) {
                Text("Use email & password instead", color = Color.White.copy(alpha = 0.9f))
            }
        }
    )
}

@Composable
private fun PinPadLayout(
    title: String,
    subtitle: String,
    filledCount: Int,
    error: String?,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    footer: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HeroGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Lock, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(4) { i ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (i < filledCount) Color.White
                                else Color.White.copy(alpha = 0.35f)
                            )
                    )
                }
            }

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(error, color = Color(0xFFFFCDD2), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.weight(1f))

            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "⌫")
            )
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .then(
                                    if (key.isNotEmpty()) Modifier.clickable {
                                        if (key == "⌫") onBackspace() else onDigit(key)
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when (key) {
                                "" -> {}
                                "⌫" -> Icon(Icons.Filled.Backspace, null, tint = Color.White)
                                else -> Text(
                                    key,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            footer?.invoke()
            Spacer(Modifier.height(16.dp))
        }
    }
}