package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.CheckBanned;

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


    }
}

//sender.sendMessage(new ComponentBuilder("").create());
