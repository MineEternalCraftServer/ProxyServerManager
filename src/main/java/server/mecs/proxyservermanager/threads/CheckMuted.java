package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckMuted extends Thread {

    public ProxyServerManager plugin;
    public String mcid;
    public static Boolean isMuted = false;

    public CheckMuted(ProxyServerManager plugin, String mcid) {
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public void run() {
        MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
        ResultSet rs = mysql.query("SELECT * FROM punish_player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                if (rs.getBoolean("isMuted")){
                    isMuted = true;
                }else if (!(rs.getBoolean("isMuted"))){
                    isMuted = false;
                }else{
                    mysql.execute("UPDATE player_data SET isMuted=false WHERE mcid='" + mcid + "';");
                }
            }

            rs.close();
        } catch (SQLException e) {
        }

        mysql.close();
    }

    public static boolean isMuted(ProxyServerManager plugin, String mcid){
        CheckMuted checkMuted = new CheckMuted(plugin, mcid);
        checkMuted.start();
        return isMuted;
    }
}
