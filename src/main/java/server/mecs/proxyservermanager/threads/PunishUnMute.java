package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PunishUnMute {
    public static void PunishUnMute(ProxyServerManager plugin, String mcid) {
        try(MySQLManager mysql = new MySQLManager(plugin, "PunishUnMute");
            ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';")) {
            if (rs.next()){
                mysql.execute("UPDATE player_data SET isMuted=false, mute_reason='' WHERE mcid='" + mcid + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
