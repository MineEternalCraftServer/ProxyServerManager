package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBanned extends Thread {

    public ProxyServerManager plugin;
    public String mcid;
    public static Boolean isBanned = false;

    public CheckBanned(ProxyServerManager plugin, String mcid) {
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public void run() {
        MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
        ResultSet rs = mysql.query("SELECT * FROM punish_player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                if (rs.getBoolean("isBanned")){
                    isBanned = true;
                }else if (!(rs.getBoolean("isBanned"))){
                    isBanned = false;
                }else{
                    mysql.execute("UPDATE player_data SET isBanned=false WHERE mcid='" + mcid + "';");
                }
            }

            rs.close();
        } catch (SQLException e) {
        }

        mysql.close();
    }

    public static boolean isBanned(ProxyServerManager plugin, String mcid){
        CheckBanned checkBanned = new CheckBanned(plugin, mcid);
        checkBanned.start();
        return isBanned;
    }
}
