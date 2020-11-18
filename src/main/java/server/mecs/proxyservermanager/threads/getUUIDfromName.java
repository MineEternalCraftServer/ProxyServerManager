package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class getUUIDfromName{

    public static String getUUIDfromName(ProxyServerManager plugin, String mcid){
        MySQLManager mysql = new MySQLManager(plugin, "getUUID");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try{
            if (rs.next()){
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        return null;
    }

}
