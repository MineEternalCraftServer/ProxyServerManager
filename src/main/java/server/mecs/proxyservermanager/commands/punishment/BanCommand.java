package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.CheckBanned;
import server.mecs.proxyservermanager.threads.PunishBan;

public class BanCommand extends Command {

    ProxyServerManager plugin = null;

    public BanCommand(ProxyServerManager plugin, String name){
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
            sender.sendMessage(new ComponentBuilder("§c/ban <player> <reason>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        if (CheckBanned.isBanned(plugin, args[0])){
            sender.sendMessage(new ComponentBuilder("§cThe player has already been banned from this server.").create());
            return;
        }

        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String reason = str.toString().trim();

        PunishBan.PunishBan(plugin, args[0], reason);

        if (CheckBanned.isBanned(plugin, args[0])){
            sender.sendMessage(new ComponentBuilder("§aThat player has been successfully banned.").create());

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            player.disconnect(new ComponentBuilder("§cYou are permanently banned from this server.\n§7Reason: §f" + reason).create());

            plugin.getProxy().getPluginManager().dispatchCommand(sender, "staff " + args[0] + " &chas been banned by " + sender.getName() + " &cfor " + reason);

            ProxyServer.getInstance().broadcast(new ComponentBuilder("§c§lA player has been removed from the server for hacking or abuse.\n" +
                    "§bThanks for reporting it!").create());
        }else{
            sender.sendMessage(new ComponentBuilder("§cFailed to banned that player.").create());
        }
    }
}

//sender.sendMessage(new ComponentBuilder("").create());
//ProxyServer.getInstance().broadcast(new ComponentBuilder("").create());
