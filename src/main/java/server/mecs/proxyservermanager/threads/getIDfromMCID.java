package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class getIDfromMCID extends Thread {

    ProxyServerManager plugin;
    String mcid;
    public static Long id;

    public getIDfromMCID(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
        id = null;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "getIDfromMCID");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                if (rs.getString("discord_link").equals("An_Unlinked_Player")) id = null;
                id = rs.getLong("discord_link");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        id = null;
    }

    public static Long getIDfromMCID(ProxyServerManager plugin, String mcid) throws InterruptedException {
        getIDfromMCID getIDfromMCID = new getIDfromMCID(plugin, mcid);
        getIDfromMCID.start();
        getIDfromMCID.join();
        return id;
    }
}
