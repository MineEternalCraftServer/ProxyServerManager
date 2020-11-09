package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;

public class getBanReason extends Thread {

    ProxyServerManager plugin = null;
    String mcid = null;
    public static String reason = null;

    public getBanReason(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "getBanReason");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");
    }

    public static String getBanReason(ProxyServerManager plugin, String mcid){
        getBanReason getBanReason = new getBanReason(plugin, mcid);
        getBanReason.start();
        return reason;
    }

}
