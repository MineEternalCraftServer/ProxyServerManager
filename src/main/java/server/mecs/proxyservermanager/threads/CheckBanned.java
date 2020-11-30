package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBanned extends Thread {

    ProxyServerManager plugin;
    String mcid;
    public static boolean result = false;

    public CheckBanned(ProxyServerManager plugin, String mcid) {
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                result =  rs.getBoolean("isBanned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        result = false;
    }

    public static boolean isBanned(ProxyServerManager plugin, String mcid) throws InterruptedException {
        CheckBanned checkBanned = new CheckBanned(plugin, mcid);
        checkBanned.start();
        checkBanned.join();
        return result;
    }
}
