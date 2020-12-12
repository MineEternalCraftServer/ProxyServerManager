package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.*;

import java.util.concurrent.ExecutionException;

public class LoginListener implements Listener {

    ProxyServerManager plugin = null;

    public LoginListener(ProxyServerManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PostLoginEvent e) throws InterruptedException, ExecutionException {
        ProxiedPlayer player = e.getPlayer();

        PlayerData.PlayerData(plugin, player);

        if (CheckBanned.isBanned(plugin, player.getName())) {
            String reason = getBanReason.getBanReason(plugin, player.getName());
            player.disconnect(new ComponentBuilder("§cYou are permanently banned from this server.\n \n§7Reason: §f" + reason).create());
            return;
        }

        if (CheckMuted.isMuted(plugin, player.getName())) {
            plugin.MuteMap.put(player.getUniqueId(), true);
        }

        LoginLog.LoginLog(plugin, e.getPlayer());

        if (player.getUniqueId().toString().length() != 32){
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "Your account has a security alert.");
        }

        if (player.hasPermission("group.owner") || player.hasPermission("group.administrator")) {
            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §c" + e.getPlayer().getName() + "§6 joined the network.").create());
            return;
        }

        if (player.hasPermission("group.moderator")) {
            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §2" + e.getPlayer().getName() + "§6 joined the network.").create());
            return;
        }

        if (player.hasPermission("group.helper")) {
            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §9" + e.getPlayer().getName() + "§6 joined the network.").create());
            return;
        }

        if (player.hasPermission("group.youtuber")) {
            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §f" + e.getPlayer().getName() + "§6 joined the network.").create());
            return;
        }

        if (player.hasPermission("group.vip+++") || player.hasPermission("group.vip++") || player.hasPermission("group.vip+") || player.hasPermission("group.vip")) {
            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §a" + e.getPlayer().getName() + "§6 joined the network.").create());
            return;
        }

        ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §7" + e.getPlayer().getName() + "§6 joined the network.").create());

    }
}
