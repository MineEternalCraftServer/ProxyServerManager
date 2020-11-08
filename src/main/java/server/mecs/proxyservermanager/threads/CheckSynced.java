package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckSynced extends Thread {

    public ProxyServerManager plugin;
    public String mcid = null;
    public Long id = null;
    public static Boolean isSynced = false;

    public CheckSynced(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public CheckSynced(ProxyServerManager plugin, Long id){
        this.plugin = plugin;
        this.id = id;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "CheckSynced");

        if (mcid != null){
            ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

            try {
                if (rs.next()){
                    if (rs.getString("discord_link") != "An_Unlinked_Player"){
                        isSynced = true;
                        return;
                    }
                    isSynced = false;
                }
            } catch (SQLException e) {
            }

            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mysql.close();
        }

        if (id != null){
            ResultSet rs = mysql.query("SELECT * FROM player_data WHERE discord_link='" + id + "';");

            try {
                if (rs.next()){
                    isSynced = true;
                    return;
                }
                isSynced = false;
            } catch (SQLException e) {
            }

            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mysql.close();
        }
    }

    public static boolean isSynced(ProxyServerManager plugin, String mcid){
        CheckSynced checkSynced = new CheckSynced(plugin, mcid);
        checkSynced.start();
        return isSynced;
    }

    public static boolean isSynced(ProxyServerManager plugin, Long id){
        CheckSynced checkSynced = new CheckSynced(plugin, id);
        checkSynced.start();
        return isSynced;
    }
}
