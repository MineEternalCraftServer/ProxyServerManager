package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.threads.CheckMuted;
import server.mecs.proxyservermanager.threads.PunishUnMute;
import server.mecs.proxyservermanager.threads.PunishmentLog;
import server.mecs.proxyservermanager.threads.getUUIDfromName;
import server.mecs.proxyservermanager.utils.getDate;

import java.util.concurrent.ExecutionException;

public class UnMuteCommand extends Command {

    ProxyServerManager plugin = null;

    public UnMuteCommand(ProxyServerManager plugin, String name) {
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
            sender.sendMessage(new ComponentBuilder("§c/unmute <player> <reason>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        if (args[0] == sender.getName()) {
            sender.sendMessage(new ComponentBuilder("§cYou can not punish yourself.").create());
            return;
        }

        try {
            if (!(CheckMuted.isMuted(plugin, args[0]))) {
                sender.sendMessage(new ComponentBuilder("§cThat player has been not muted from this server.").create());
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            PunishUnMute.PunishUnMute(plugin, args[0]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (!(CheckMuted.isMuted(plugin, args[0]))) {
                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                    plugin.MuteMap.remove(player.getUniqueId());
                }

                sender.sendMessage(new ComponentBuilder("§aThat player has been successfully unmuted.").create());
                StaffMessage.sendStaffMessage(plugin, args[0] + " §chas been unmuted by " + sender.getName());

                String uuid = getUUIDfromName.getUUIDfromName(plugin, args[0]);
                String date = getDate.getDate();

                if (sender instanceof ProxiedPlayer) {
                    PunishmentLog.PunishmentLog(plugin, sender.getName(), ((ProxiedPlayer) sender).getUniqueId().toString(), args[0], uuid, "UNMUTE", "", date);
                } else {
                    PunishmentLog.PunishmentLog(plugin, sender.getName(), "Console", args[0], uuid, "UNMUTE", "", date);
                }
            } else {
                sender.sendMessage(new ComponentBuilder("§cFailed to unmuted that player.").create());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
