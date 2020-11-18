package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PunishMute{

    public static void PunishMute(ProxyServerManager plugin, String mcid, String reason){
        MySQLManager mysql = new MySQLManager(plugin, "PunishMute");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try{
            if (rs.next()){
                mysql.execute("UPDATE player_data SET isMuted=true, mute_reason='" + reason + "' WHERE mcid='" + mcid + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
    }
}
