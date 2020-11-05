package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.PlayerDataThread;

public class LoginListener implements Listener {

    ProxyServerManager plugin = null;

    public LoginListener(ProxyServerManager plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PostLoginEvent e){
        PlayerDataThread playerdata = new PlayerDataThread(e.getPlayer(), plugin);
        playerdata.start();
        ProxyServer.getInstance().broadcast(e.getPlayer().getDisplayName() + "§ehas joined the network.");
    }
}
