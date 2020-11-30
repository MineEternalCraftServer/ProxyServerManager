package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class getIDfromMCID implements Callable<Long> {

    ProxyServerManager plugin;
    String mcid;
    public static Long id;

    public getIDfromMCID(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
        id = null;
    }

    public static Long getIDfromMCID(ProxyServerManager plugin, String mcid) throws InterruptedException, ExecutionException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Long> futureresult = ex.submit(new getIDfromMCID(plugin, mcid));
        return futureresult.get();
    }

    @Override
    public Long call() throws Exception {
        MySQLManager mysql = new MySQLManager(plugin, "getIDfromMCID");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                if (rs.getString("discord_link").equals("An_Unlinked_Player")) id = null;
                return rs.getLong("discord_link");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        return null;
    }
}
