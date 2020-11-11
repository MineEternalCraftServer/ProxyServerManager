package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PunishBan extends Thread {

    ProxyServerManager plugin = null;
    String mcid = null;
    String reason = null;

    public PunishBan(ProxyServerManager plugin, String mcid, String reason){
        this.plugin = plugin;
        this.mcid = mcid;
        this.reason = reason;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "PunishBan");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()){

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
