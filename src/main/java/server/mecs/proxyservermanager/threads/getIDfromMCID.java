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
    }

    public static Long getIDfromMCID(ProxyServerManager plugin, String mcid){
        MySQLManager mysql = new MySQLManager(plugin, "getIDfromMCID");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                if (rs.getString("discord_link").equals("An_Unlinked_Player")) return null;
                id = rs.getLong("discord_link");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        return id;
    }
}
