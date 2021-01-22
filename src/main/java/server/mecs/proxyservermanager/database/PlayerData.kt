package server.mecs.proxyservermanager.database

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.connection.ProxiedPlayer
import server.mecs.proxyservermanager.getTime.getDate


object PlayerData {
    fun checkPlayerData(con: MongoDBManager, player: ProxiedPlayer) {
        val count = con.queryCount("{uuid:'${player.uniqueId}'}")
        when {
            count == 0L -> {
                con.queryInsertOne(
                        "{'mcid':'${player.name}', " +
                                "'uuid':'${player.uniqueId}', " +
                                "'Discord':'null', " +
                                "'Created':'${getDate()}'}"
                )
            }

            count == 1L -> {
                con.queryUpdateOne("{uuid:'${player.uniqueId}'}", "{\$set:{mcid:'${player.name}'}}")
            }

            count >= 2L -> {
                ProxyServer.getInstance().getPlayer(player.name).disconnect(*ComponentBuilder("§cInternal Error: Duplicated Player Data.").create())
                ProxyServer.getInstance().logger.info("§c${player.name} Duplicated Player Data.")
            }
        }
    }
}