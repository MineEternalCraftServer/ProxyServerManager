package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class AccountSync {
    public static void AccountSync(ProxyServerManager plugin, String player, Long id) {
        try(MySQLManager mysql = new MySQLManager(plugin, "AccountSync")){
            mysql.execute("UPDATE player_data SET discord_link='" + id + "' WHERE mcid='" + player + "';");
        }
    }
}
