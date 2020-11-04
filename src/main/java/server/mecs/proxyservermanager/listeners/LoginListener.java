package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.threads.PlayerData;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(PostLoginEvent e){
        PlayerData playerdata = new PlayerData();
        playerdata.start();
        ProxyServer.getInstance().broadcast(e.getPlayer().getDisplayName() + "§ehas joined the network.");
    }
}
