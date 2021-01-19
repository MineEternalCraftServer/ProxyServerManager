package server.mecs.proxyservermanager.listeners

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import server.mecs.proxyservermanager.ProxyServerManager
import server.mecs.proxyservermanager.ProxyServerManager.Companion.getDate
import server.mecs.proxyservermanager.database.MongoDBManager

class ChatListener(var plugin: ProxyServerManager) : Listener {
    @EventHandler
    fun onChat(e: ChatEvent) {
        val player = e.sender as? ProxiedPlayer ?: return
        MongoDBManager.executeChatQueue(
                "{'mcid':'${player.name}'" +
                "'uuid':'${player.uniqueId}', " +
                "'server':'${player.server}', " +
                "'message':'${e.message}', " +
                "'date', '${getDate()}'}"
        )
    }
}