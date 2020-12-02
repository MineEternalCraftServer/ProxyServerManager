package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class CheckBanned implements Callable<Boolean> {

    ProxyServerManager plugin;
    String mcid;

    public CheckBanned(ProxyServerManager plugin, String mcid) {
        this.plugin = plugin;
        this.mcid = mcid;
    }


    public static boolean isBanned(ProxyServerManager plugin, String mcid) throws ExecutionException, InterruptedException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Boolean> futureresult = ex.submit(new CheckBanned(plugin, mcid));
        return futureresult.get();
    }

    @Override
    public Boolean call() throws Exception {

        try (MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
            ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';")) {
            if (rs.next()) {
                return rs.getBoolean("isBanned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
