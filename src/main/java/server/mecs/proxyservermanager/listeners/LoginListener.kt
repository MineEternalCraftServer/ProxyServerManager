package server.mecs.proxyservermanager.listeners

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import server.mecs.proxyservermanager.ProxyServerManager
import server.mecs.proxyservermanager.ProxyServerManager.Companion.es
import server.mecs.proxyservermanager.database.MongoDBManager
import server.mecs.proxyservermanager.database.PlayerData.checkPlayerData
import server.mecs.proxyservermanager.getTime.getDate

class LoginListener(private val plugin: ProxyServerManager) : Listener {
    @EventHandler
    fun onLogin(e: PostLoginEvent) {
        val player = e.player
        es.execute {
            MongoDBManager(plugin, "PlayerData").use { con ->
                checkPlayerData(con, player)
            }
        }

        MongoDBManager.executeLoginQueue(
                "{'mcid':'${e.player.name}', " +
                "'uuid':'${e.player.uniqueId}', " +
                "'address':'${player.socketAddress}'" +
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
        ProxyServer.getInstance().broadcast(*ComponentBuilder("§b>§d>§b> $prefix${player.name}§6 joined the network.").create())
    }
}