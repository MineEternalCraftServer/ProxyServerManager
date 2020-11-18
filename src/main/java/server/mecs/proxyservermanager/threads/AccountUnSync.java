package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class AccountUnSync {

    public static void AccountUnSync(ProxyServerManager plugin, String player, Long id){
        MySQLManager mysql = new MySQLManager(plugin, "AccountUnSync");

        if (player != null){
            mysql.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE mcid='" + player + "';");
        }

        if (id != null){
            mysql.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE discord_link='" + id + "';");
        }
        mysql.close();
    }
}
