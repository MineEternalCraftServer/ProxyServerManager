package server.mecs.proxyservermanager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import server.mecs.proxyservermanager.commands.discord.McToDiscord;
import server.mecs.proxyservermanager.commands.privatemessage.ReplyCommand;
import server.mecs.proxyservermanager.commands.privatemessage.TellCommand;
import server.mecs.proxyservermanager.commands.report.Report;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.discord.Discord;
import server.mecs.proxyservermanager.threads.PlayerData;

import java.util.HashMap;
import java.util.UUID;

public final class ProxyServerManager extends Plugin {

    public static Discord discord;
    public static HashMap<UUID, Boolean> MuteMap = new HashMap<>();
    public static HashMap<UUID, String> NickMap = new HashMap<>();
    public static HashMap<String, String> history;

    @Override
    public void onEnable() {
        // Plugin startup logic
        for ( String command : new String[]{"tell", "msg", "message", "m", "w", "t"}) {
            getProxy().getPluginManager().registerCommand(this, new TellCommand(this, command));
        }
        for ( String command : new String[]{"reply", "r"}) {
            getProxy().getPluginManager().registerCommand(this, new ReplyCommand(this, command));
        }
        for (String command : new String[]{"discord"}){
            getProxy().getPluginManager().registerCommand(this, new McToDiscord(this, command));
        }
        for (String command : new String[]{"staff"}){
            getProxy().getPluginManager().registerCommand(this, new StaffMessage(this, command));
        }
        for (String command : new String[]{"report"}){
            getProxy().getPluginManager().registerCommand(this, new Report(this, command));
        }
        history = new HashMap<>();
        loadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    void loadConfig(){
        Configuration config = new ConfigFile(this).getConfig();
        try{
            discord.token = config.getString("discord.token");
            discord.guildID = config.getLong("discord.guild");
            discord.receivereportChannelID = config.getLong("discord.receivereportchannel");
            discord.staffmessageChannelID = config.getLong("discord.staffmessagechannel");

            discord.plugin = this;
            discord.setup();
        }catch (NullPointerException e){
            e.printStackTrace();
            getLogger().info(e.getLocalizedMessage());
        }
    }

    @EventHandler public void onLogin(PostLoginEvent e){
        PlayerData playerdata = new PlayerData();
        playerdata.start();
        ProxyServer.getInstance().broadcast(e.getPlayer().getDisplayName() + "§ehas joined the network.");
    }

    @EventHandler public void onLogout(PlayerDisconnectEvent e){
        NickMap.remove(e.getPlayer().getUniqueId());
        MuteMap.remove(e.getPlayer().getUniqueId());
        ProxyServer.getInstance().broadcast(e.getPlayer().getDisplayName() + "§ehas left the network.");
    }

    @EventHandler public void onMove(ServerConnectedEvent e){
        
    }

    @EventHandler public void onChat(ChatEvent e){
        if (e.isCancelled() || e.isProxyCommand())return;

        Connection player = e.getSender();

        if (!(player instanceof ProxiedPlayer))return;

        if (this.MuteMap.containsKey(((ProxiedPlayer) player).getUniqueId())){
            ((ProxiedPlayer) player).sendMessage(new ComponentBuilder("§cYou have been muted.").create());
            e.setCancelled(true);
        }
    }

    public void putHistory(String reciever, String sender) {
        history.put(reciever, sender);
    }

    public String getHistory(String reciever) {
        return history.get(reciever);
    }
}
