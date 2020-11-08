package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.CheckBanned;

public class LoginListener implements Listener {

    ProxyServerManager plugin = null;

    public LoginListener(ProxyServerManager plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PostLoginEvent e){
        if (CheckBanned.isBanned(plugin, e.getPlayer().getName())){

        }

        ProxyServer.getInstance().broadcast(e.getPlayer().getDisplayName() + "§ehas joined the network.");
    }
}
