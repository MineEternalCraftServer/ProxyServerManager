package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;

public class MoveListener implements Listener {

    ProxyServerManager plugin = null;

    @EventHandler
    public void onMove(ServerConnectedEvent e){
    }
}
