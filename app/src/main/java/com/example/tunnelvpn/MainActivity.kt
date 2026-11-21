package com.example.tunnelvpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.tunnelvpn.model.SampleServers
import com.example.tunnelvpn.model.ServerConfig
import com.example.tunnelvpn.ui.ConnectScreen
import com.example.tunnelvpn.ui.ServerListScreen
import com.example.tunnelvpn.vpn.TunnelManager
import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var tunnelManager: TunnelManager
    private var pendingConfig: String? = null

    // Android requires the user to approve the VPN permission dialog once;
    // this launcher handles that system prompt.
    private val vpnPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                pendingConfig?.let { tunnelManager.connect(it) }
            }
            pendingConfig = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tunnelManager = TunnelManager(this)

        setContent {
            MaterialTheme {
                AppRoot(
                    tunnelManager = tunnelManager,
                    requestVpnPermission = ::requestVpnPermission
                )
            }
        }
    }

    /**
     * android.net.VpnService.prepare() returns an Intent only if the user
     * hasn't already granted this app VPN permission (or granted it to a
     * different VPN app). If it returns null we're already clear to connect.
     */
    private fun requestVpnPermission(rawConfig: String) {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            pendingConfig = rawConfig
            vpnPermissionLauncher.launch(intent)
        } else {
            tunnelManager.connect(rawConfig)
        }
    }
}

private enum class Screen { CONNECT, SERVER_LIST }

@Composable
private fun AppRoot(tunnelManager: TunnelManager, requestVpnPermission: (String) -> Unit) {
    var screen by remember { mutableStateOf(Screen.CONNECT) }
    var servers by remember { mutableStateOf(SampleServers.list) }
    var selectedId by remember { mutableStateOf(servers.first().id) }
    var isConnected by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }

    // Poll the real tunnel state so the UI reflects what GoBackend reports,
    // not just what the button was last tapped to.
    LaunchedEffect(Unit) {
        while (true) {
            isConnected = tunnelManager.state == Tunnel.State.UP
            if (isConnected) seconds++
            delay(1000)
        }
    }

    val selectedServer = servers.first { it.id == selectedId }

    when (screen) {
        Screen.CONNECT -> ConnectScreen(
            isConnected = isConnected,
            elapsedLabel = formatElapsed(seconds),
            uploadKbps = 550,
            downloadMbps = 40.0,
            selectedServer = selectedServer,
            onToggleConnection = {
                if (isConnected) {
                    tunnelManager.disconnect()
                    seconds = 0
                } else {
                    if (selectedServer.wgConfigText.isNotBlank()) {
                        requestVpnPermission(selectedServer.wgConfigText)
                    }
                }
            },
            onOpenServerList = { screen = Screen.SERVER_LIST }
        )

        Screen.SERVER_LIST -> ServerListScreen(
            servers = servers,
            selectedId = selectedId,
            onSelect = {
                selectedId = it.id
                screen = Screen.CONNECT
            },
            onToggleFavorite = { toggled ->
                servers = servers.map {
                    if (it.id == toggled.id) it.copy(isFavorite = !it.isFavorite) else it
                }
            }
        )
    }
}

private fun formatElapsed(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
