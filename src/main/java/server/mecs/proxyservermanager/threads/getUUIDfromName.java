package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

public class getUUIDfromName implements Callable<String> {

    ProxyServerManager plugin;
    String mcid;

    public getUUIDfromName(ProxyServerManager plugin, String mcid){
        this.plugin = plugin;
        this.mcid = mcid;
    }

    public static String getUUIDfromName(ProxyServerManager plugin, String mcid) throws InterruptedException, ExecutionException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<String> futureresult = ex.submit(new getUUIDfromName(plugin, mcid));
        return futureresult.get();
    }

    @Override
    public String call() throws Exception {
        try( MySQLManager mysql = new MySQLManager(plugin, "getUUID");
             ResultSet rs = mysql.query("SELECT * FROM player_data WHERE mcid='" + mcid + "';")){
            if (rs.next()){
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
