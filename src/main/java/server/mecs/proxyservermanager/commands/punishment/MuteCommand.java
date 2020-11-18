package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.threads.CheckMuted;
import server.mecs.proxyservermanager.threads.PunishMute;
import server.mecs.proxyservermanager.threads.PunishmentLog;
import server.mecs.proxyservermanager.threads.getUUIDfromName;
import server.mecs.proxyservermanager.utils.getDate;

public class MuteCommand extends Command {

    ProxyServerManager plugin = null;

    public MuteCommand(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            if (!(sender.hasPermission("server.punish"))) {
                sender.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
                return;
            }

            if (!(args.length >= 2)) {
                sender.sendMessage(new ComponentBuilder("").create());
                sender.sendMessage(new ComponentBuilder("§c/mute <player> <reason>").create());
                sender.sendMessage(new ComponentBuilder("").create());
                return;
            }

            if (args[0] == sender.getName()) {
                sender.sendMessage(new ComponentBuilder("§cYou can not punish yourself.").create());
                return;
            }

            if (CheckMuted.isMuted(plugin, args[0])) {
                sender.sendMessage(new ComponentBuilder("§cThat player has already been muted from this server.").create());
                return;
            }

            StringBuilder str = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String reason = str.toString().trim();

            PunishMute.PunishMute(plugin, args[0], reason);

            if (CheckMuted.isMuted(plugin, args[0])) {

                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
                    plugin.MuteMap.put(player.getUniqueId(), true);
                }

                sender.sendMessage(new ComponentBuilder("§aThat player has been successfully muted.").create());
                StaffMessage.sendStaffMessage(plugin, args[0] + " &chas been permanently muted by " + sender.getName() + " &cfor " + reason);

                String uuid = getUUIDfromName.getUUIDfromName(plugin, args[0]);
                String date = getDate.getDate();

                if (sender instanceof ProxiedPlayer) {
                    PunishmentLog.PunishmentLog(plugin, sender.getName(), ((ProxiedPlayer) sender).getUniqueId().toString(), args[0], uuid, "MUTE", reason, date);
                } else {
                    PunishmentLog.PunishmentLog(plugin, sender.getName(), "Console", args[0], uuid, "MUTE", reason, date);
                }
            } else {
                sender.sendMessage(new ComponentBuilder("§cFailed to muted that player.").create());
            }
        });
    }
}
