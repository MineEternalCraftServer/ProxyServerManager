package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.threads.CheckBanned;
import server.mecs.proxyservermanager.threads.PunishBan;
import server.mecs.proxyservermanager.threads.PunishmentLog;
import server.mecs.proxyservermanager.threads.getUUIDfromName;
import server.mecs.proxyservermanager.utils.getDate;

import java.util.concurrent.ExecutionException;

public class BanCommand extends Command {

    ProxyServerManager plugin = null;

    public BanCommand(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender.hasPermission("server.punish"))) {
            sender.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
            return;
        }

        if (!(args.length >= 2)) {
            sender.sendMessage(new ComponentBuilder("").create());
            sender.sendMessage(new ComponentBuilder("§c/ban <player> <reason>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        if (args[0].equals(sender.getName())) {
            sender.sendMessage(new ComponentBuilder("§cYou can not punish yourself.").create());
            return;
        }

        try {
            if (CheckBanned.isBanned(plugin, args[0])) {
                sender.sendMessage(new ComponentBuilder("§cThat player has already been banned from this server.").create());
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String reason = str.toString().trim();

        try {
            PunishBan.PunishBan(plugin, args[0], reason);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (!(CheckBanned.isBanned(plugin, args[0]))) {
                sender.sendMessage(new ComponentBuilder("§cFailed to banned that player.").create());
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            player.disconnect(new ComponentBuilder("§cYou are permanently banned from this server.\n \n§7Reason: §f" + reason).create());
        }

        sender.sendMessage(new ComponentBuilder("§aThat player has been successfully banned.").create());
        StaffMessage.sendStaffMessage(plugin, args[0] + " §chas been permanently banned by " + sender.getName() + " §cfor " + reason);

        ProxyServer.getInstance().broadcast(new ComponentBuilder("§c§lA player has been removed from the server for hacking or abuse.\n" +
                "§bThanks for reporting it!").create());

        String uuid = null;
        try {
            uuid = getUUIDfromName.getUUIDfromName(plugin, args[0]);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        String date = getDate.getDate();

        try {
            if (sender instanceof ProxiedPlayer) {
                PunishmentLog.PunishmentLog(plugin, sender.getName(), ((ProxiedPlayer) sender).getUniqueId().toString(), args[0], uuid, "BAN", reason, date);
            } else {
                PunishmentLog.PunishmentLog(plugin, sender.getName(), "Console", args[0], uuid, "BAN", reason, date);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//sender.sendMessage(new ComponentBuilder("").create());
//ProxyServer.getInstance().broadcast(new ComponentBuilder("").create());
