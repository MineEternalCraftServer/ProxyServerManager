package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class getBanReason extends Thread{

    ProxyServerManager plugin;
    String mcid;
    public static String result = null;

    public getBanReason(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
        result = null;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "getBanReason");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");
        try {
            result = rs.getString("ban_reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        result = null;
    }

    public static String getBanReason(ProxyServerManager plugin, String mcid) throws InterruptedException {
        getBanReason getBanReason = new getBanReason(plugin, mcid);
        getBanReason.start();
        getBanReason.join();
        return result;
    }

}
