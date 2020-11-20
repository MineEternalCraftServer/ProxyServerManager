package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.LogoutLog;

public class LogoutListener implements Listener {

    ProxyServerManager plugin = null;

    public LogoutListener(ProxyServerManager plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogout(PlayerDisconnectEvent e) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            ProxiedPlayer player = e.getPlayer();

            plugin.MuteMap.remove(player.getUniqueId());

            LogoutLog.LogoutLog(plugin, player);

            if (player.hasPermission("group.owner") || player.hasPermission("group.administrator")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §c" + e.getPlayer().getName() + "§6 left the network.").create());
                return;
            }

            if (player.hasPermission("group.moderator")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §2" + e.getPlayer().getName() + "§6 left the network.").create());
                return;
            }

            if (player.hasPermission("group.helper")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §1" + e.getPlayer().getName() + "§6 left the network.").create());
                return;
            }

            if (player.hasPermission("group.youtuber")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §f" + e.getPlayer().getName() + "§6 left the network.").create());
                return;
            }

            if (player.hasPermission("group.vip+++") || player.hasPermission("group.vip++") || player.hasPermission("group.vip+") || player.hasPermission("group.vip")){
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §a" + e.getPlayer().getName() + "§6 left the network.").create());
                return;
            }

            ProxyServer.getInstance().broadcast(new ComponentBuilder("§b>§d>§b> §7" + e.getPlayer().getName() + "§6 left the network.").create());

        });
    }
}
