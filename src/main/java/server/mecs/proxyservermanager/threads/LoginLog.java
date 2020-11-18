package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;
import server.mecs.proxyservermanager.utils.getDate;

public class LoginLog{

    public static void LoginLog(ProxyServerManager plugin, ProxiedPlayer player){
        String address = player.getAddress().getHostString();

        if (address == null){
            plugin.getLogger().info(player.getName() + "のIPアドレスの取得に失敗");
        }

        MySQLManager mysql = new MySQLManager(plugin, "LoginLog");

        mysql.execute("INSERT INTO login_log (mcid,uuid,address,date) " +
                "VALUES ('" + player.getName() + "','" + player.getUniqueId() + "','" + address + "','" + getDate.getDate() + "');");

        mysql.close();
    }
}
