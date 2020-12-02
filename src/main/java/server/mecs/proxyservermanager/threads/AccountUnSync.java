package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class AccountUnSync extends Thread{

    ProxyServerManager plugin;
    String player;
    Long id;

    public AccountUnSync(ProxyServerManager plugin, String player, Long id){
        this.plugin = plugin;
        this.player = player;
        this.id = id;
    }

    public void run(){
        try(MySQLManager mysql = new MySQLManager(plugin, "AccountUnSync")) {
            if (player != null){
                mysql.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE mcid='" + player + "';");
            }

            if (id != null){
                mysql.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE discord_link='" + id + "';");
            }
        }
    }

    public static void AccountUnSync(ProxyServerManager plugin, String player, Long id) throws InterruptedException {
        AccountUnSync accountUnSync = new AccountUnSync(plugin, player, id);
        accountUnSync.start();
        accountUnSync.join();
    }
}
