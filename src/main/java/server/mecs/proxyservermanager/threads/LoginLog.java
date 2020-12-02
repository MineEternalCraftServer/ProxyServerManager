package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;
import server.mecs.proxyservermanager.utils.getDate;

public class LoginLog extends Thread{

    ProxyServerManager plugin;
    ProxiedPlayer player;

    public LoginLog(ProxyServerManager plugin, ProxiedPlayer player){
        this.plugin = plugin;
        this.player = player;
    }

    public void run(){
        try(MySQLManager mysql = new MySQLManager(plugin, "LoginLog")) {
            String address = player.getAddress().getHostString();

            if (address == null) {
                plugin.getLogger().info(player.getName() + "のIPアドレスの取得に失敗");
            }

            mysql.execute("INSERT INTO login_log (mcid,uuid,address,date) " +
                    "VALUES ('" + player.getName() + "','" + player.getUniqueId() + "','" + address + "','" + getDate.getDate() + "');");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoginLog(ProxyServerManager plugin, ProxiedPlayer player) throws InterruptedException {
        LoginLog loginLog = new LoginLog(plugin, player);
        loginLog.start();
        loginLog.join();
    }
}
