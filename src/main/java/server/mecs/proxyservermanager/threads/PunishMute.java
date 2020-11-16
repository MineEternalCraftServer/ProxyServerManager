package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PunishMute extends Thread{

    ProxyServerManager plugin = null;
    String mcid = null;
    String reason = null;

    public PunishMute(ProxyServerManager plugin, String mcid, String reason){
        this.plugin = plugin;
        this.mcid = mcid;
        this.reason = reason;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "PunishMute");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try{
            if (rs.next()){
                mysql.execute("UPDATE player_data SET isMuted=true WHERE mcid='" + mcid + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
    }

    public static void PunishMute(ProxyServerManager plugin, String mcid, String reason){
        PunishMute punishMute = new PunishMute(plugin, mcid, reason);
        punishMute.start();
    }
}
