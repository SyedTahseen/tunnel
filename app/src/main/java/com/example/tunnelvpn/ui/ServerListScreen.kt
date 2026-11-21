package com.example.tunnelvpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunnelvpn.model.ServerConfig

@Composable
fun ServerListScreen(
    servers: List<ServerConfig>,
    selectedId: String,
    onSelect: (ServerConfig) -> Unit,
    onToggleFavorite: (ServerConfig) -> Unit
) {
    var showFavoritesOnly by remember { mutableStateOf(false) }
    val visible = if (showFavoritesOnly) servers.filter { it.isFavorite } else servers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(visible) { server ->
                ServerRow(
                    server = server,
                    isSelected = server.id == selectedId,
                    onSelect = { onSelect(server) },
                    onToggleFavorite = { onToggleFavorite(server) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showFavoritesOnly,
                onClick = { showFavoritesOnly = false },
                label = { Text("All") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = showFavoritesOnly,
                onClick = { showFavoritesOnly = true },
                label = { Text("Favourite") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ServerRow(
    server: ServerConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF151515))
            .clickable { onSelect() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(server.flagEmoji, fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(server.countryName, color = Color.White)
            Text("Speed: ${server.speedMbps} mb/s", color = Color.Gray, fontSize = 12.sp)
        }
        Icon(
            imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
            contentDescription = "Favorite",
            tint = if (server.isFavorite) Color(0xFFFFC107) else Color.Gray,
            modifier = Modifier
                .padding(end = 12.dp)
                .clickable { onToggleFavorite() }
        )
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isSelected) Color(0xFFFFC107) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black)
        }
    }
}
