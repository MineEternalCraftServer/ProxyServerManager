package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class AccountSync extends Thread {

    ProxyServerManager plugin = null;
    ProxiedPlayer player = null;
    Long id = null;

    public AccountSync(ProxyServerManager plugin,ProxiedPlayer player, Long id){
        this.plugin = plugin;
        this.player = player;
        this.id = id;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "AccountSync");
        mysql.execute("UPDATE player_data SET discord_link='" + id + "' WHERE mcid='" + player.getName() + "';");
        mysql.close();
    }

    public static void AccountSync(ProxyServerManager plugin,ProxiedPlayer player, Long id){
        AccountSync accountSync = new AccountSync(plugin, player, id);
        accountSync.start();
    }
}
