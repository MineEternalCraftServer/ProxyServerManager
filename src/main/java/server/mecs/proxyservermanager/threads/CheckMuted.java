package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class CheckMuted implements Callable<Boolean> {

    public ProxyServerManager plugin;
    public String mcid;

    public CheckMuted(ProxyServerManager plugin, String mcid) {
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public static boolean isMuted(ProxyServerManager plugin, String mcid) throws InterruptedException, ExecutionException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Boolean> futureresult = ex.submit(new CheckMuted(plugin, mcid));
        return futureresult.get();
    }

    @Override
    public Boolean call() throws Exception {
        MySQLManager mysql = new MySQLManager(plugin, "CheckBanned");
        ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

        try {
            if (rs.next()) {
                return rs.getBoolean("isMuted");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            mysql.close();
        }
        return false;
    }
}
