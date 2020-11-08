package server.mecs.proxyservermanager.threads;

import server.mecs.proxyservermanager.ProxyServerManager;

public class PlayerData extends Thread {

    ProxyServerManager plugin = null;
    String player = null;

    public PlayerData(ProxyServerManager plugin, String player){

    }
}
