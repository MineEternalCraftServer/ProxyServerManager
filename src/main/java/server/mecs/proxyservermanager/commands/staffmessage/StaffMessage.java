package server.mecs.proxyservermanager.commands.staffmessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import server.mecs.proxyservermanager.ProxyServerManager;

public class StaffMessage extends Command {

    ProxyServerManager plugin;

    public StaffMessage(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("server.staffmessage")) {
            sender.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("§c/staff <message>").create());
            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {

            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String message = str.toString().trim();
            message = ChatColor.translateAlternateColorCodes('&', message);

            sendStaffMessage(plugin, message);


            plugin.discord.staffmessage(message);
        });
    }

    public static void sendStaffMessage(Plugin plugin, String message) {
        for (ProxiedPlayer players : plugin.getProxy().getPlayers()) {
            if (!players.hasPermission("server.staffmessage")) continue;
            players.sendMessage(new ComponentBuilder("§b§l[STAFF]§r " + message).create());
        }
    }
}
