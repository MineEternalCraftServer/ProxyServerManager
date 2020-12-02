package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class AccountSync extends Thread{

    ProxyServerManager plugin;
    String player;
    Long id;

    public AccountSync(ProxyServerManager plugin, String player, Long id){
        this.plugin = plugin;
        this. player = player;
        this.id = id;
    }

    public void run(){
        try(MySQLManager mysql = new MySQLManager(plugin, "AccountSync")){
            mysql.execute("UPDATE player_data SET discord_link='" + id + "' WHERE mcid='" + player + "';");
        }
    }

    public static void AccountSync(ProxyServerManager plugin, String player, Long id) throws InterruptedException {
        AccountSync accountSync = new AccountSync(plugin, player, id);
        accountSync.start();
        accountSync.join();
    }
}
