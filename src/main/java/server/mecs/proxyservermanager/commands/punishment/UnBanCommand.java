package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.threads.CheckBanned;
import server.mecs.proxyservermanager.threads.PunishUnBan;
import server.mecs.proxyservermanager.threads.PunishmentLog;
import server.mecs.proxyservermanager.threads.getUUIDfromName;
import server.mecs.proxyservermanager.utils.getDate;

import java.util.concurrent.ExecutionException;

public class UnBanCommand extends Command {

    ProxyServerManager plugin = null;

    public UnBanCommand(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender.hasPermission("server.punish"))) {
            sender.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(new ComponentBuilder("").create());
            sender.sendMessage(new ComponentBuilder("§c/unban <player> <reason>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        if (args[0].equals(sender.getName())) {
            sender.sendMessage(new ComponentBuilder("§cYou can not punish yourself.").create());
            return;
        }

        new Thread(() -> {

            if (!(CheckBanned.isBanned(plugin, args[0]))) {
                sender.sendMessage(new ComponentBuilder("§cThat player has been not banned from this server.").create());
                return;
            }

            try {
                PunishUnBan.PunishUnBan(plugin, args[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                if (!(CheckBanned.isBanned(plugin, args[0]))) {
                    sender.sendMessage(new ComponentBuilder("§aThat player has been successfully unbanned.").create());
                    StaffMessage.sendStaffMessage(plugin, args[0] + " §chas been unbanned by " + sender.getName());

                    String uuid = getUUIDfromName.getUUIDfromName(plugin, args[0]);
                    String date = getDate.getDate();

                    if (sender instanceof ProxiedPlayer) {
                        PunishmentLog.PunishmentLog(plugin, sender.getName(), ((ProxiedPlayer) sender).getUniqueId().toString(), args[0], uuid, "UNBAN", "", date);
                    } else {
                        PunishmentLog.PunishmentLog(plugin, sender.getName(), "Console", args[0], uuid, "UNBAN", "", date);
                    }
                } else {
                    sender.sendMessage(new ComponentBuilder("§cFailed to unbanned that player.").create());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
