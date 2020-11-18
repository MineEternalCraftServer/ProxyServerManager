package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
            PlayerData.PlayerData(plugin, e.getPlayer());

            if (CheckBanned.isBanned(plugin, e.getPlayer().getName())) {
                String reason = getBanReason.getBanReason(plugin, e.getPlayer().getName());
                e.getPlayer().disconnect(new ComponentBuilder("§cYou are permanently banned from this server.\n§7Reason: §f" + reason).create());
                return;
            }

            if (CheckMuted.isMuted(plugin, e.getPlayer().getName())) {
                plugin.MuteMap.put(e.getPlayer().getUniqueId(), true);
            }

            LoginLog.LoginLog(plugin, e.getPlayer());

            ProxyServer.getInstance().broadcast(new ComponentBuilder(e.getPlayer().getDisplayName() + " §ehas joined the network.").create());
        });
    }
}
