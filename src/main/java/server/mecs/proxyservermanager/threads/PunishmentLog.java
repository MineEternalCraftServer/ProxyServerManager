package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class PunishmentLog extends Thread{

    ProxyServerManager plugin = null;
    String executioner_mcid = null;
    String executioner_uuid = null;
    String target_mcid = null;
    String target_uuid = null;
    String punish_type = null;
    String punish_reason = null;
    String punish_date = null;

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
        MySQLManager mysql = new MySQLManager(plugin, "PunishmentLog");

        mysql.execute("INSERT INTO punishment_log " +
                "(executioner_mcid,executioner_uuid,target_mcid,target_uuid,punish_type,punish_reason,punish_date) " +
                "VALUES ('" + executioner_mcid + "','" + executioner_uuid + "','" + target_mcid + "','" + target_uuid + "','" + punish_type + "','" + punish_reason + "','" + punish_date + "');");

        mysql.close();
    }

    public static void PunishmentLog(ProxyServerManager plugin, String executioner_mcid, String executioner_uuid, String target_mcid, String target_uuid, String punish_type, String punish_reason, String punish_date){
        PunishmentLog punishmentLog = new PunishmentLog(plugin, executioner_mcid, executioner_uuid, target_mcid, target_uuid, punish_type, punish_reason, punish_date);
        punishmentLog.start();
    }
}
