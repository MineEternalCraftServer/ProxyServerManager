package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class getBanReason {

    public static String getBanReason(ProxyServerManager plugin, String mcid){
        MySQLManager mysql = new MySQLManager(plugin, "getBanReason");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");
        try {
            return rs.getString("ban_reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        return null;
    }

}
