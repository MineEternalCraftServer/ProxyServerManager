package server.mecs.proxyservermanager.listeners;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.ProxyServerManager;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    ProxyServerManager plugin;

    public static HashMap<UUID, Long> CoolTime = new HashMap<>();

    public ChatListener(ProxyServerManager plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(ChatEvent e){
        Connection player = e.getSender();

        if (!(player instanceof ProxiedPlayer))return;

        if (e.isCommand() || e.isProxyCommand()){
            UUID uuid = ((ProxiedPlayer) player).getUniqueId();
            if (CoolTime.containsKey(uuid)){
                if (CoolTime.get(uuid) + 1000 * 5 >= System.currentTimeMillis()){
                    CoolTime.remove(uuid);
                }
            }else{
                Long timeleft = CoolTime.get(uuid) + 1000 * 5 / 1000 - System.currentTimeMillis() / 1000;
                ((ProxiedPlayer) player).sendMessage(new ComponentBuilder("§cYour cooldown has " + timeleft + "§c seconds left.").create());
                e.setCancelled(true);
            }
            CoolTime.put(uuid, System.currentTimeMillis());
            return;
        }

        if (plugin.MuteMap.containsKey(((ProxiedPlayer) player).getUniqueId())){
            ((ProxiedPlayer) player).sendMessage(new ComponentBuilder("§cYou have been muted.").create());
            e.setCancelled(true);
        }
    }
}
