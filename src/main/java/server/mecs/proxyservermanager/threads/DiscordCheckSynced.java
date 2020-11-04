package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscordCheckSynced extends Thread {

    ProxyServerManager plugin = null;
    String mcid = null;

    public  DiscordCheckSynced(ProxyServerManager plugin, String mcid){
        this.mcid = mcid;
        this.plugin = plugin;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "DiscordCheck");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()){
                
            }
        } catch (SQLException e) {
            return;
        }
    }
}
