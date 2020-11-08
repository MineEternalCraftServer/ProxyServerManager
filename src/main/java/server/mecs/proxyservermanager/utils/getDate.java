package server.mecs.proxyservermanager.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class getDate {
    public static String getDate(){
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return current.format(formatter);
    }
}
