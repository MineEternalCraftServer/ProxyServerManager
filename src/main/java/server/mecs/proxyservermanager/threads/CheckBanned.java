package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBanned {
    public static boolean isBanned(ProxyServerManager plugin, String mcid) {
        try (MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
             ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';")) {
            if (rs.next()) {
                return rs.getBoolean("isBanned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
