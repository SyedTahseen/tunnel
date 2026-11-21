package com.example.tunnelvpn.vpn

import android.content.Context
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import java.io.BufferedReader
import java.io.StringReader

/**
 * Thin wrapper around the WireGuard GoBackend engine.
 *
 * GoBackend does the real work: it owns the VpnService, builds the TUN
 * interface, and runs the Go userspace WireGuard implementation that
 * encrypts/decrypts packets over UDP to your server. This class just
 * gives the UI a simple connect(config) / disconnect() / state surface.
 */
class TunnelManager(context: Context) {

    private val backend: Backend = GoBackend(context.applicationContext)

    private val tunnel = object : Tunnel {
        override fun getName(): String = "tunnelvpn"
        override fun onStateChange(newState: Tunnel.State) {
            _state = newState
        }
    }

    @Volatile
    private var _state: Tunnel.State = Tunnel.State.DOWN
    val state: Tunnel.State get() = _state

    /**
     * `rawConfig` is a wg-quick style config block, see ServerConfig.wgConfigText.
     * Must be called after the user has granted the VpnService.prepare() intent
     * (handled in MainActivity).
     */
    fun connect(rawConfig: String) {
        val config = Config.parse(BufferedReader(StringReader(rawConfig)))
        backend.setState(tunnel, Tunnel.State.UP, config)
    }

    fun disconnect() {
        backend.setState(tunnel, Tunnel.State.DOWN, null)
    }

    /** Bytes sent/received since the tunnel came up, if the backend exposes stats. */
    fun statistics() = try {
        backend.getStatistics(tunnel)
    } catch (e: Exception) {
        null
    }
}
