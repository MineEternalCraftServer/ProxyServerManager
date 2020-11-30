package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PunishUnMute extends Thread{

    ProxyServerManager plugin;
    String mcid;

    public PunishUnMute(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "PunishUnMute");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()){
                mysql.execute("UPDATE player_data SET isMuted=false, mute_reason='' WHERE mcid='" + mcid + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
    }

    public static void PunishUnMute(ProxyServerManager plugin, String mcid) throws InterruptedException {
        PunishUnMute punishUnMute = new PunishUnMute(plugin, mcid);
        punishUnMute.start();
        punishUnMute.join();
    }

}
