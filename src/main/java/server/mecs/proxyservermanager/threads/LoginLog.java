package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;
import server.mecs.proxyservermanager.utils.getDate;

public class LoginLog extends Thread{

    ProxyServerManager plugin = null;
    ProxiedPlayer player = null;

    public LoginLog(ProxyServerManager plugin, ProxiedPlayer player){
        this.plugin = plugin;
        this.player = player;
    }

    public void run(){
        String address = player.getAddress().getHostString();

        if (address == null){
            plugin.getLogger().info(player.getName() + "のIPアドレスの取得に失敗");
        }

        MySQLManager mysql = new MySQLManager(plugin, "LoginLog");

        mysql.execute("INSERT INTO login_log (mcid,uuid,address,date) " +
                "VALUES ('" + player.getName() + "','" + player.getUniqueId() + "','" + address + "','" + getDate.getDate() + "');");

        mysql.close();
    }

    public static void LoginLog(ProxyServerManager plugin, ProxiedPlayer player){
        LoginLog loginLog = new LoginLog(plugin, player);
        loginLog.start();
    }
}
