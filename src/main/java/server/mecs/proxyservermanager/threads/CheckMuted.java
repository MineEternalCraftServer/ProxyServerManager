package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckMuted extends Thread {

    public ProxyServerManager plugin;
    public String mcid;
    public static boolean result = false;

    public CheckMuted(ProxyServerManager plugin, String mcid) {
        this.plugin = plugin;
        this.mcid = mcid;
        result = false;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                result =  rs.getBoolean("isMuted");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        result = false;
    }

    public static boolean isMuted(ProxyServerManager plugin, String mcid) throws InterruptedException {
        CheckMuted checkMuted = new CheckMuted(plugin, mcid);
        checkMuted.start();
        checkMuted.join();
        return result;
    }
}
