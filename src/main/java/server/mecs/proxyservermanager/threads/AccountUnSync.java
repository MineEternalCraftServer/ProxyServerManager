package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class AccountUnSync extends Thread {

    ProxyServerManager plugin = null;
    String player = null;
    Long id = null;

    public AccountUnSync(ProxyServerManager plugin, Long id){
        this.plugin = plugin;
        this.id = id;
    }

    public AccountUnSync(ProxyServerManager plugin, String player){
        this.plugin = plugin;
        this.player = player;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "AccountUnSync");

        if (player != null){
            mysql.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE mcid='" + player + "';");
        }

        if (id != null){
            mysql.execute("UPDATE player_data SET discord_link='An_Unlinked_Player' WHERE discord_link='" + id + "';");
        }
        mysql.close();
    }

    public static void AccountUnSync(ProxyServerManager plugin, String player){
        AccountUnSync accountUnSync = new AccountUnSync(plugin, player);
        accountUnSync.start();
    }

    public static void AccountUnSync(ProxyServerManager plugin, Long id){
        AccountUnSync accountUnSync = new AccountUnSync(plugin, id);
        accountUnSync.start();
    }

}
