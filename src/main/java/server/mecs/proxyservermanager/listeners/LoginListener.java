package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.*;

public class LoginListener implements Listener {

    ProxyServerManager plugin = null;

    public LoginListener(ProxyServerManager plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PostLoginEvent e){
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            ProxiedPlayer player = e.getPlayer();

            PlayerData.PlayerData(plugin, player);

            if (CheckBanned.isBanned(plugin, player.getName())) {
                String reason = getBanReason.getBanReason(plugin, player.getName());
                e.getPlayer().disconnect(new ComponentBuilder("§cYou are permanently banned from this server.\n§7Reason: §f" + reason).create());
                return;
            }

            if (CheckMuted.isMuted(plugin, player.getName())) {
                plugin.MuteMap.put(player.getUniqueId(), true);
            }

            LoginLog.LoginLog(plugin, e.getPlayer());

            if (player.hasPermission("group.owner") || player.hasPermission("group.administrator")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §c" + e.getPlayer().getName() + "§6 joined the network.").create());
                return;
            }

            if (player.hasPermission("group.moderator")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §2" + e.getPlayer().getName() + "§6 joined the network.").create());
                return;
            }

            if (player.hasPermission("group.helper")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §1" + e.getPlayer().getName() + "§6 joined the network.").create());
                return;
            }

            if (player.hasPermission("group.youtuber")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §f" + e.getPlayer().getName() + "§6 joined the network.").create());
                return;
            }

            if (player.hasPermission("group.vip+++") || player.hasPermission("group.vip++") || player.hasPermission("group.vip+") || player.hasPermission("group.vip")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §a" + e.getPlayer().getName() + "§6 joined the network.").create());
                return;
            }

            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §7" + e.getPlayer().getName() + "§6 joined the network.").create());

        });
    }
}
