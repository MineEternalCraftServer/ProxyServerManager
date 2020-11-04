package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerData extends Thread {
    public void run(){
        ProxyServerManager plugin = null;
        ProxiedPlayer p = null;
        MySQLManager mysql = new MySQLManager(plugin, "PlayerData");
        ResultSet rs_player_data = mysql.query("SELECT * FROM player_data WHERE uuid='" + p.getUniqueId() + "';");
        ResultSet rs_punish_player_data = mysql.query("SELECT * FROM punish_player_data WHERE uuid='" + p.getUniqueId() + "';");
        ResultSet rs_check_nicked = mysql.query("SELECT * FROM nicknamer_data_nick WHERE _Key='" + p.getUniqueId() + "';");

        try {
            if (rs_player_data.next() && rs_punish_player_data.next()){
                if (rs_player_data.getString("mcid") != p.getName()){
                    mysql.execute("UPDATE player_data SET mcid='" + p.getName() + "' WHERE uuid='" + p.getUniqueId() + "';");
                    mysql.execute("UPDATE punish_player_data SET mcid='" + p.getName() + "' WHERE uuid='" + p.getUniqueId() + "';");
                }
                if (rs_punish_player_data.getString("ban_util") == "true"){
                    p.disconnect("§c§lYou are permanently banned from this server.\n" +
                            "§f§lReason: " + rs_punish_player_data.getString("ban_reason"));
                }
                if (rs_punish_player_data.getString("mute_util") == "true"){
                    plugin.MuteMap.put(p.getUniqueId(), true);
                }
            }
        } catch (SQLException e) {
            mysql.execute("INSERT INTO player_data (mcid,uuid,discord_link) " +
                    "VALUES ('" + p.getName() + "','" + p.getUniqueId() + "','An_Unlinked_Player');");
            mysql.execute("INSERT INTO punish_player_data (mcid,uuid,ban_util,mute_util,ban_reason,mute_reason) " +
                    "VALUE ('" + p.getName() + "','" + p.getUniqueId() + "','false','false','','');");
        }

        try {
            if (rs_check_nicked.next()){
                plugin.NickMap.put(p.getUniqueId(), rs_check_nicked.getString("_Value"));
            }
        } catch (SQLException e) {
            plugin.NickMap.put(p.getUniqueId(), p.getDisplayName());
        }
        try {
            rs_player_data.close();
            rs_punish_player_data.close();
            rs_check_nicked.close();
            mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
