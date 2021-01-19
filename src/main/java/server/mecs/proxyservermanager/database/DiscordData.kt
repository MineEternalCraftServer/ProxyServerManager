package server.mecs.proxyservermanager.database

object DiscordData {
    fun syncAccount(con: MySQLManager, mcid: String, id: Long) {
        con.execute("UPDATE player_data SET discord_link='$id' WHERE mcid='$mcid';")
    }

    fun unsyncAccount(con: MySQLManager, mcid: String?, id: Long?) {
        if (mcid != null) {
            con.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE mcid='$mcid';")
        }

        if (id != null) {
            con.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE discord_link='$id';")
        }
    }

    fun isSynced(con: MySQLManager, mcid: String?, id: Long?): Boolean {
        var result = false
        if (mcid != null) {
            con.query("SELECT * FROM player_data WHERE mcid='$mcid';").use { rs ->
                if (rs?.next()!!) {
                    result = rs.getString("discord_link") != "An_Unlinked_Player"
                }
            }
        }

        if (id != null) {
            con.query("SELECT * FROM player_data WHERE discord_link='$id';").use { rs ->
                result = rs?.next()!!
            }
        }
        return result
    }
}