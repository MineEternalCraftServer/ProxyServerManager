package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.*;
import server.mecs.proxyservermanager.utils.getDate;

public class UnMuteCommand extends Command {

    ProxyServerManager plugin = null;

    public UnMuteCommand(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender.hasPermission("server.punish"))){
            sender.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
            return;
        }

        if (args.length != 1){
            sender.sendMessage(new ComponentBuilder("").create());
            sender.sendMessage(new ComponentBuilder("§c/unmute <player> <reason>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        if (args[0] == sender.getName()){
            sender.sendMessage(new ComponentBuilder("§cYou can not punish yourself.").create());
            return;
        }

        if (!(CheckMuted.isMuted(plugin, args[0]))){
            sender.sendMessage(new ComponentBuilder("§cThat player has been not muted from this server.").create());
            return;
        }

        PunishUnMute.PunishUnMute(plugin, args[0]);

        if (!(CheckMuted.isMuted(plugin, args[0]))){
            sender.sendMessage(new ComponentBuilder("§aThat player has been successfully unmuted.").create());
            plugin.getProxy().getPluginManager().dispatchCommand(sender, "staff " + args[0] + " &chas been unmuted by " + sender.getName() + ".");

            String uuid = getUUIDfromName.getUUIDfromName(plugin, args[0]);
            String date = getDate.getDate();

            if (sender instanceof ProxiedPlayer){
                PunishmentLog.PunishmentLog(plugin, sender.getName(), ((ProxiedPlayer) sender).getUniqueId().toString(), args[0], uuid, "UNMUTE", "", date);
            }else{
                PunishmentLog.PunishmentLog(plugin, sender.getName(), "Console", args[0], uuid, "UNMUTE", "", date);
            }
        }else{
            sender.sendMessage(new ComponentBuilder("§cFailed to unmuted that player.").create());
        }
    }
}
