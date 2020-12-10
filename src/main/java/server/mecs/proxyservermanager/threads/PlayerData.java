package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerData extends Thread{

    ProxyServerManager plugin;
    ProxiedPlayer player;

    public PlayerData(ProxyServerManager plugin, ProxiedPlayer player){
        this.plugin = plugin;
        this.player = player;
    }

    public void run(){
        try( MySQLManager mysql = new MySQLManager(plugin, "PlayerData");
             ResultSet rs = mysql.query("SELECT * FROM player_data WHERE uuid='" + player.getUniqueId() + "';")) {
            if (rs.next()){
                if (!rs.getString("mcid").equals(player.getName())){
                    mysql.execute("UPDATE player_data SET mcid='" + player.getName() + "' WHERE uuid='" + player.getUniqueId() + "';");
                }
                return;
            }
            mysql.execute("INSERT INTO player_data (mcid,uuid,discord_link,isBanned,isMuted,ban_reason,mute_reason) " +
                    "VALUES ('" + player.getName() + "','" + player.getUniqueId() + "','An_Unlinked_Player','0','0','','');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void PlayerData(ProxyServerManager plugin, ProxiedPlayer player) throws InterruptedException {
        PlayerData playerData = new PlayerData(plugin, player);
        playerData.start();
        playerData.join();
    }
}
