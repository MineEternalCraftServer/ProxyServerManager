package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PunishUnBan extends Thread{

    ProxyServerManager plugin;
    String mcid;

    public PunishUnBan(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "PunishUnBan");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()){
                mysql.execute("UPDATE player_data SET isBanned=false, ban_reason='' WHERE mcid='" + mcid + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
    }

    public static void PunishUnBan(ProxyServerManager plugin, String mcid) throws InterruptedException {
        PunishUnBan punishUnBan = new PunishUnBan(plugin, mcid);
        punishUnBan.start();
        punishUnBan.join();
    }

}