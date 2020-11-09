package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
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
        PlayerData.PlayerData(plugin, e.getPlayer());

        if (CheckBanned.isBanned(plugin, e.getPlayer().getName())){
            String reason = getBanReason.getBanReason(plugin, e.getPlayer().getName());
            e.getPlayer().disconnect("§c§lYou are permanently banned from this server.\n§f§lReason: " + reason);
            return;
        }

        if ( CheckMuted.isMuted(plugin, e.getPlayer().getName())){
            plugin.MuteMap.put(e.getPlayer().getUniqueId(), true);
        }

        LoginLog.LoginLog(plugin, e.getPlayer());

        ProxyServer.getInstance().broadcast(e.getPlayer().getDisplayName() + "§ehas joined the network.");
    }
}
