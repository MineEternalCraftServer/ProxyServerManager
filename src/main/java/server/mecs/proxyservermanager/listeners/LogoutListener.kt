package server.mecs.proxyservermanager.listeners

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import server.mecs.proxyservermanager.ProxyServerManager.Companion.getDate
import server.mecs.proxyservermanager.database.MongoDBManager

class LogoutListener : Listener {
    @EventHandler
    fun onLogout(e: PlayerDisconnectEvent) {
        val player = e.player

        MongoDBManager.executeLogoutQueue(
                "{'mcid':'${player.name}', " +
                "'uuid':'${player.uniqueId}', " +
                "'date':'${getDate()}'}"
        )

        val prefix = when {
            player.hasPermission("group.owner") -> "§c[OWNER] "
            player.hasPermission("group.administrator") -> "§c[ADMIN] "
            player.hasPermission("group.moderator") -> "§2[MOD] "
            player.hasPermission("group.helper") -> "§9[HELPER] "
            player.hasPermission("group.youtuber") -> "§c[§f§lYT§c] "
            player.hasPermission("group.vip+++") -> "§a[VIP§0+++§a] "
            player.hasPermission("group.vip++") -> "§a[VIP§b++§a] "
            player.hasPermission("group.vip+") -> "§a[VIP§6+§a] "
            player.hasPermission("group.vip") -> "§a[VIP§a] "
            else -> "§7"
        }
        ProxyServer.getInstance().broadcast(*ComponentBuilder("§b>§d>§b> $prefix${player.name}§6 left the network.").create())
    }
}