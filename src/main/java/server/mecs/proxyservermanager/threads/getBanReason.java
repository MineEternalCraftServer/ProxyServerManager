package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class getBanReason implements Callable<String> {

    ProxyServerManager plugin;
    String mcid;

    public getBanReason(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public static String getBanReason(ProxyServerManager plugin, String mcid) throws InterruptedException, ExecutionException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<String> futureresult = ex.submit(new getBanReason(plugin, mcid));
        return futureresult.get();
    }

    @Override
    public String call() throws Exception {
        MySQLManager mysql = new MySQLManager(plugin, "getBanReason");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");
        try {
            if (rs.next()) {
                return rs.getString("ban_reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        return null;
    }
}
