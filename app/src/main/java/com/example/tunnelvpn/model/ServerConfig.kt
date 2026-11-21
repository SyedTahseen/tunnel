package com.example.tunnelvpn.model

/**
 * One entry in the server list screen.
 *
 * `wgConfigText` is a full wg-quick style config block for THIS server,
 * e.g.:
 *
 * [Interface]
 * PrivateKey = <client private key>
 * Address = 10.8.0.2/32
 * DNS = 1.1.1.1
 *
 * [Peer]
 * PublicKey = <server public key>
 * Endpoint = vpn-us1.yourdomain.com:51820
 * AllowedIPs = 0.0.0.0/0, ::/0
 * PersistentKeepalive = 25
 *
 * In production, don't hardcode these — fetch them over HTTPS from a
 * server you control, after the user authenticates, and generate a
 * fresh client keypair per device/install.
 */
data class ServerConfig(
    val id: String,
    val countryName: String,
    val flagEmoji: String,
    val speedMbps: Double,
    val isFavorite: Boolean = false,
    val wgConfigText: String
)

object SampleServers {
    val list = listOf(
        ServerConfig(
            id = "us1",
            countryName = "United States",
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
            speedMbps = 7.80,
            isFavorite = false,
            wgConfigText = """
                [Interface]
                PrivateKey = REPLACE_WITH_CLIENT_PRIVATE_KEY
                Address = 10.8.0.2/32
                DNS = 1.1.1.1

                [Peer]
                PublicKey = REPLACE_WITH_SERVER_PUBLIC_KEY
                Endpoint = us1.example.com:51820
                AllowedIPs = 0.0.0.0/0, ::/0
                PersistentKeepalive = 25
            """.trimIndent()
        ),
        ServerConfig(
            id = "ca1",
            countryName = "Canada",
            flagEmoji = "\uD83C\uDDE8\uD83C\uDDE6",
            speedMbps = 7.90,
            wgConfigText = "" // fill in like above
        ),
        ServerConfig(
            id = "uk1",
            countryName = "United Kingdom",
            flagEmoji = "\uD83C\uDDEC\uD83C\uDDE7",
            speedMbps = 6.15,
            isFavorite = true,
            wgConfigText = ""
        ),
        ServerConfig(
            id = "ar1",
            countryName = "Argentina",
            flagEmoji = "\uD83C\uDDE6\uD83C\uDDF7",
            speedMbps = 5.84,
            wgConfigText = ""
        )
    )
}
