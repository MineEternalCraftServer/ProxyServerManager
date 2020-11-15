package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.PunishmentLog;
import server.mecs.proxyservermanager.threads.getUUIDfromName;
import server.mecs.proxyservermanager.utils.getDate;

public class KickCommand extends Command {

    ProxyServerManager plugin = null;

    public KickCommand(ProxyServerManager plugin, String name){
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender.hasPermission("server.punish"))){
            sender.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
            return;
        }

        if (!(args.length >= 2)){
            sender.sendMessage(new ComponentBuilder("").create());
            sender.sendMessage(new ComponentBuilder("§c/kick <player> <reason>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String reason = str.toString().trim();

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
        player.disconnect(new ComponentBuilder("§cYou have been kicked.\n§7Reason: §f" + reason).create());

        plugin.getProxy().getPluginManager().dispatchCommand(sender, "staff " + args[0] + " &chas been kicked by " + sender.getName() + " &cfor " + reason);

        String uuid = getUUIDfromName.getUUIDfromName(plugin, args[0]);
        String date = getDate.getDate();

        if (sender instanceof ProxiedPlayer){
            PunishmentLog.PunishmentLog(plugin, sender.getName(), ((ProxiedPlayer) sender).getUniqueId().toString(), args[0], uuid, "KICK", reason, date);
        }else{
            PunishmentLog.PunishmentLog(plugin, sender.getName(), "Console", args[0], uuid, "KICK", reason, date);
        }
    }
}
