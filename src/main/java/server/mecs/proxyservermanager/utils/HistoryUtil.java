package server.mecs.proxyservermanager.utils;

import java.util.HashMap;

public class HistoryUtil {


    public static HashMap<String, String> history = new HashMap<>();

    public void putHistory(String reciever, String sender) {
        history.put(reciever, sender);
    }

    public String getHistory(String reciever) {
        return history.get(reciever);
    }
}
