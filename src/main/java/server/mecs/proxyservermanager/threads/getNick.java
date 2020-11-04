package server.mecs.proxyservermanager.threads;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class getNick extends Thread {

    ProxyServerManager plugin = null;
    ProxiedPlayer p = null;

    public getNick(ProxyServerManager plugin, ProxiedPlayer player){
        this.plugin = plugin;
        this.p = player;
    }

    public void run(){
        MySQLManager mysql = new MySQLManager(plugin, "getNick");
        ResultSet rs = mysql.query("SELECT * FROM nicknamer_data_nick WHERE _Key= " + p.getUniqueId());

        if (rs == null)return;

        try {
            if (rs.next()){
                plugin.NickMap.put(p.getUniqueId(), rs.getString("_Value"));
            }
        } catch (SQLException e) {
        }

        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mysql.close();
    }
}
