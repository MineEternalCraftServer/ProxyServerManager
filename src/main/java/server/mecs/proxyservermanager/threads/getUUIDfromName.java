package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class getUUIDfromName extends Thread{

    ProxyServerManager plugin;
    String mcid;
    public static String uuid;

    public getUUIDfromName(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
        uuid = null;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "getUUID");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try{
            if (rs.next()){
                uuid = rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        uuid = null;
    }

    public static String getUUIDfromName(ProxyServerManager plugin, String mcid) throws InterruptedException {
        getUUIDfromName getUUIDfromName = new getUUIDfromName(plugin, mcid);
        getUUIDfromName.start();
        getUUIDfromName.join();
        return uuid;
    }

}
