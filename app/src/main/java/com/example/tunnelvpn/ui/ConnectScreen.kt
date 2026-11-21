package com.example.tunnelvpn.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
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
import com.example.tunnelvpn.model.ServerConfig

@Composable
fun ConnectScreen(
    isConnected: Boolean,
    elapsedLabel: String,
    uploadKbps: Int,
    downloadMbps: Double,
    selectedServer: ServerConfig,
    onToggleConnection: () -> Unit,
    onOpenServerList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Text(
            "Your IP: 77.239.70.113",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            elapsedLabel,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )

        Spacer(Modifier.weight(1f))

        // The connect "orb" — tap to toggle the tunnel on/off.
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(Color(0xFFB24BF3), Color(0xFF4B6EF5), Color(0xFF4BD3F3), Color(0xFFB24BF3))
                    )
                )
                .clickable { onToggleConnection() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Power, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF151515))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatColumn(label = "Upload", value = "$uploadKbps Kb/s")
            StatColumn(label = "Download", value = "${downloadMbps} Mb/s")
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF151515))
                    .padding(16.dp)
            ) {
                Text("I choose", color = Color.Gray, fontSize = 12.sp)
                Text("Fastest", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF151515))
                    .padding(16.dp)
                    .clickable { onOpenServerList() }
            ) {
                Text("Location selection", color = Color.Gray, fontSize = 12.sp)
                Text(
                    "${selectedServer.flagEmoji} ${selectedServer.countryName}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}
