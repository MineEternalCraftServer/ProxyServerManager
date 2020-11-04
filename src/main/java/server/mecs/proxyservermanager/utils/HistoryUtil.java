package server.mecs.proxyservermanager.utils;

import server.mecs.proxyservermanager.ProxyServerManager;

public class HistoryUtil {

    ProxyServerManager plugin;

    public void putHistory(String reciever, String sender) {
        plugin.history.put(reciever, sender);
    }

    public String getHistory(String reciever) {
        return plugin.history.get(reciever);
    }
}
