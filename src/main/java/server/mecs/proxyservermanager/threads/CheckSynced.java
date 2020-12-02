package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class CheckSynced implements Callable<Boolean> {

    ProxyServerManager plugin;
    String mcid;
    Long id;

    public CheckSynced(ProxyServerManager plugin, String mcid, Long id){
        this.plugin = plugin;
        this.mcid = mcid;
        this.id = id;
    }

    public static boolean isSynced(ProxyServerManager plugin, String mcid, Long id) throws InterruptedException, ExecutionException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Boolean> futureresult = ex.submit(new CheckSynced(plugin, mcid, id));
        return futureresult.get();
    }

    @Override
    public Boolean call() throws Exception {
        try(MySQLManager mysql = new MySQLManager(plugin, "CheckSynced")) {
            if (mcid != null){
                ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';");

                try {
                    if (rs.next()){
                        return  !rs.getString("discord_link").equals("An_Unlinked_Player");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    mysql.close();
                }
            }

            if (id != null){
                ResultSet rs = mysql.query("SELECT * FROM player_data WHERE discord_link='" + id + "';");

                try {
                    return rs.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    mysql.close();
                }
            }
        }
        return false;
    }
}
