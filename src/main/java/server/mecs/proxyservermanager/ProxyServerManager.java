package server.mecs.proxyservermanager;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import server.mecs.proxyservermanager.commands.discord.McToDiscord;
import server.mecs.proxyservermanager.commands.privatemessage.ReplyCommand;
import server.mecs.proxyservermanager.commands.privatemessage.TellCommand;
import server.mecs.proxyservermanager.commands.report.Report;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.discord.Discord;
import server.mecs.proxyservermanager.listeners.ChatListener;
import server.mecs.proxyservermanager.listeners.LoginListener;
import server.mecs.proxyservermanager.listeners.LogoutListener;
import server.mecs.proxyservermanager.listeners.MoveListener;

import java.util.HashMap;
import java.util.UUID;

public final class ProxyServerManager extends Plugin {

    public static Discord discord;
    public static HashMap<String, String> history;
    public static HashMap<UUID, Boolean> MuteMap = new HashMap<>();
    public static HashMap<UUID, String> NickMap = new HashMap<>();

    public static Long currentTime = System.currentTimeMillis();
    public static HashMap<UUID, Long> CoolTime = new HashMap<>();

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

        getProxy().getPluginManager().registerListener(this, new LoginListener(this));
        getProxy().getPluginManager().registerListener(this, new LogoutListener());
        getProxy().getPluginManager().registerListener(this, new MoveListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());

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
}
