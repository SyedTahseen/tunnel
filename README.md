# Tunnel VPN — Android skeleton (WireGuard engine)

A minimal, working VPN client skeleton: Compose UI based on your mockup,
backed by the real **WireGuard** tunnel engine (`com.wireguard.android:tunnel`,
Apache-2.0). It only talks to WireGuard servers **you** control — there's no
hidden backend, no third-party proxy list, no bundled server credentials.

## What's here
- `MainActivity.kt` — handles the one-time `VpnService.prepare()` permission
  dialog Android requires, then hands a config to `TunnelManager`.
- `vpn/TunnelManager.kt` — thin wrapper around WireGuard's `GoBackend`, which
  owns the actual `VpnService` and does the encryption/tunneling.
- `ui/ConnectScreen.kt`, `ui/ServerListScreen.kt` — the two screens from your
  mockup (connect orb + stats, and the country list with favorites).
- `model/ServerConfig.kt` — one WireGuard config block per server. **Replace
  the placeholder keys/endpoints with real ones before building.**

## Setting up a server (5 minutes, on any Ubuntu/Debian box)
```bash
sudo apt install wireguard
wg genkey | tee server_private.key | wg pubkey > server_public.key
```
Create `/etc/wireguard/wg0.conf` on the server:
```ini
[Interface]
PrivateKey = <server_private.key contents>
Address = 10.8.0.1/24
ListenPort = 51820
PostUp = iptables -A FORWARD -i wg0 -j ACCEPT; iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
PostDown = iptables -D FORWARD -i wg0 -j ACCEPT; iptables -t nat -D POSTROUTING -o eth0 -j MASQUERADE

[Peer]
PublicKey = <client's public key — generate one per install, see below>
AllowedIPs = 10.8.0.2/32
```
```bash
sudo systemctl enable --now wg-quick@wg0
```

## Generating a client config
```bash
wg genkey | tee client_private.key | wg pubkey > client_public.key
```
Add the client's public key as a `[Peer]` on the server, then fill in
`ServerConfig.wgConfigText` (or better — fetch it from your own backend over
HTTPS after login, instead of hardcoding it in the app).

## Building
Open in Android Studio (Hedgehog+), let Gradle sync, run on a device/emulator
with API 26+.

## Next steps for a production app
- Serve configs from your own authenticated HTTPS API instead of hardcoding
  them — generate a fresh client keypair per install and only send that
  install the matching server-side peer entry.
- Add a persistent notification while the VPN foreground service is running
  (required on Android 8+; a basic one needs to be started from
  `TunnelManager`/a dedicated `Service` subclass).
- Add reconnect-on-network-change and a kill switch (block traffic if the
  tunnel drops) via `VpnService.Builder` options.
- Consider `WireGuardTunnelLibrary`'s `Tunnel.State` callbacks to drive the
  "connecting / connected / error" states shown to the user.
