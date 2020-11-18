package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.database.MySQLManager;

public class PunishmentLog{

    public static void PunishmentLog(ProxyServerManager plugin, String executioner_mcid, String executioner_uuid, String target_mcid, String target_uuid, String punish_type, String punish_reason, String punish_date){
        MySQLManager mysql = new MySQLManager(plugin, "PunishmentLog");

        mysql.execute("INSERT INTO punish_log " +
                "(executioner_mcid,executioner_uuid,target_mcid,target_uuid,punish_type,punish_reason,punish_date) " +
                "VALUES ('" + executioner_mcid + "','" + executioner_uuid + "','" + target_mcid + "','" + target_uuid + "','" + punish_type + "','" + punish_reason + "','" + punish_date + "');");

        mysql.close();
    }
}
