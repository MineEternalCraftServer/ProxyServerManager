package server.mecs.proxyservermanager.database

import net.md_5.bungee.api.connection.ProxiedPlayer

object PlayerData {
    fun checkPlayerData(con: MySQLManager, player: ProxiedPlayer) {
        con.query("SELECT * FROM player_data WHERE uuid='" + player.uniqueId + "';").use { rs ->
            if (rs?.next()!!) {
                if (rs.getString("mcid") != player.name) {
                    con.execute("UPDATE player_data SET mcid='" + player.name + "' WHERE uuid='" + player.uniqueId + "';")
                }
                return
            }
            con.execute("INSERT INTO player_data (mcid,uuid,discord_link,isBanned,isMuted,ban_reason,mute_reason) " +
                    "VALUES ('${player.name}','${player.uniqueId}','An_Unlinked_Player',false,false,'','');")
        }
    }

    fun getIDfromMCID(con: MySQLManager, mcid: String): Long? {
        var result: Long? = null
        con.query("SELECT * FROM player_data WHERE mcid='$mcid';").use { rs ->
            if (rs?.next()!!) {
                result = if (rs.getString("discord_link") == "An_Unlinked_Player") null
                else rs.getLong("discord_link")
            }
            return result
        }
    }

}