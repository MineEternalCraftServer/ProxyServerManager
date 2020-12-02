package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class PunishmentLog extends Thread{

    ProxyServerManager plugin;
    String executioner_mcid;
    String executioner_uuid;
    String target_mcid;
    String target_uuid;
    String punish_type;
    String punish_reason;
    String punish_date;

    public PunishmentLog(ProxyServerManager plugin, String executioner_mcid, String executioner_uuid, String target_mcid, String target_uuid, String punish_type, String punish_reason, String punish_date){
        this.plugin = plugin;
        this.executioner_mcid = executioner_mcid;
        this.executioner_uuid = executioner_uuid;
        this.target_mcid = target_mcid;
        this.target_uuid = target_uuid;
        this.punish_type = punish_type;
        this.punish_reason = punish_reason;
        this.punish_date = punish_date;
    }

    public void run(){
        try(MySQLManager mysql = new MySQLManager(plugin, "PunishmentLog")) {
            mysql.execute("INSERT INTO punish_log " +
                    "(executioner_mcid,executioner_uuid,target_mcid,target_uuid,punish_type,punish_reason,punish_date) " +
                    "VALUES ('" + executioner_mcid + "','" + executioner_uuid + "','" + target_mcid + "','" + target_uuid + "','" + punish_type + "','" + punish_reason + "','" + punish_date + "');");

        }
    }

    public static void PunishmentLog(ProxyServerManager plugin, String executioner_mcid, String executioner_uuid, String target_mcid, String target_uuid, String punish_type, String punish_reason, String punish_date) throws InterruptedException {
        PunishmentLog punishmentLog = new PunishmentLog(plugin, executioner_mcid, executioner_uuid, target_mcid, target_uuid, punish_type, punish_reason, punish_date);
        punishmentLog.start();
        punishmentLog.join();
    }
}
