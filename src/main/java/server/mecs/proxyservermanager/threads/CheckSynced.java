package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckSynced extends Thread{

    ProxyServerManager plugin;
    String mcid;
    Long id;
    public static boolean result = false;

    public CheckSynced(ProxyServerManager plugin, String mcid, Long id){
        this.plugin = plugin;
        this.mcid = mcid;
        this.id = id;
        result = false;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "CheckSynced");

        if (mcid != null){
            ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

            try {
                if (rs.next()){
                    result = !rs.getString("discord_link").equals("An_Unlinked_Player");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                mysql.close();
            }
        }

        if (id != null){
            ResultSet rs = mysql.query("SELECT * FROM player_data WHERE discord_link='" + id + "';");

            try {
                result = rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                mysql.close();
            }
        }
        result = false;
    }

    public static boolean isSynced(ProxyServerManager plugin, String mcid, Long id) throws InterruptedException {
        CheckSynced checkSynced = new CheckSynced(plugin, mcid, id);
        checkSynced.start();
        checkSynced.join();
        return result;
    }
}
