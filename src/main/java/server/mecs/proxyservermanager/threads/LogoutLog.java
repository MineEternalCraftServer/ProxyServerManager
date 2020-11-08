package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;
import server.mecs.proxyservermanager.utils.getDate;

public class LogoutLog extends Thread {

    ProxyServerManager plugin = null;
    ProxiedPlayer player = null;

    public LogoutLog(ProxyServerManager plugin, ProxiedPlayer player){
        this.plugin = plugin;
        this.player = player;
    }

    public void run(){
        String address = player.getAddress().getHostString();

        if (address == null){
            plugin.getLogger().info(player.getName() + "のIPアドレスの取得に失敗");
        }

        MySQLManager mysql = new MySQLManager(plugin, "LogoutLog");

        mysql.execute("INSERT INTO logout_log (mcid,uuid,address,date) " +
                "VALUES ('" + player.getName() + "','" + player.getUniqueId() + "','" + address + "','" + getDate.getDate() + "');");

        mysql.close();
    }

    public static void LogoutLog(ProxyServerManager plugin, ProxiedPlayer player){
        LogoutLog logoutLog = new LogoutLog(plugin, player);
        logoutLog.start();
    }
}
