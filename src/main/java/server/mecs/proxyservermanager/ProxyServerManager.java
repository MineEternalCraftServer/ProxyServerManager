package server.mecs.proxyservermanager;

import net.md_5.bungee.api.plugin.Plugin;
import server.mecs.proxyservermanager.commands.discord.McToDiscord;
import server.mecs.proxyservermanager.commands.privatemessage.ReplyCommand;
import server.mecs.proxyservermanager.commands.privatemessage.TellCommand;
import server.mecs.proxyservermanager.commands.punishment.*;
import server.mecs.proxyservermanager.commands.report.Report;
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage;
import server.mecs.proxyservermanager.discord.Discord;
import server.mecs.proxyservermanager.listeners.ChatListener;
import server.mecs.proxyservermanager.listeners.LoginListener;
import server.mecs.proxyservermanager.listeners.LogoutListener;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server.mecs.proxyservermanager.database.MongoDBManager.setupChatLogBlockingQueue;

public final class ProxyServerManager extends Plugin {

    public static Discord discord;
    public static HashMap<UUID, Boolean> MuteMap = new HashMap<>();

    public static ExecutorService es = Executors.newCachedThreadPool();

    @Override
    public void onEnable() {
        // Plugin startup logic

        discord = new Discord(this);

        for (String command : new String[]{"tell", "msg", "message", "m", "w", "t"}) {
            getProxy().getPluginManager().registerCommand(this, new TellCommand(this, command));
        }
        for (String command : new String[]{"reply", "r"}) {
            getProxy().getPluginManager().registerCommand(this, new ReplyCommand(this, command));
        }
        for (String command : new String[]{"discord"}) {
            getProxy().getPluginManager().registerCommand(this, new McToDiscord(this, command));
        }
        for (String command : new String[]{"staff"}) {
            getProxy().getPluginManager().registerCommand(this, new StaffMessage(this, command));
        }
        for (String command : new String[]{"report"}) {
            getProxy().getPluginManager().registerCommand(this, new Report(this, command));
        }
        for (String command : new String[]{"ban"}) {
            getProxy().getPluginManager().registerCommand(this, new BanCommand(this, command));
        }
        for (String command : new String[]{"mute"}) {
            getProxy().getPluginManager().registerCommand(this, new MuteCommand(this, command));
        }
        for (String command : new String[]{"tempban"}) {
            getProxy().getPluginManager().registerCommand(this, new TempBanCommand(this, command));
        }
        for (String command : new String[]{"tempmute"}) {
            getProxy().getPluginManager().registerCommand(this, new TempMuteCommand(this, command));
        }
        for (String command : new String[]{"kick"}) {
            getProxy().getPluginManager().registerCommand(this, new KickCommand(this, command));
        }
        for (String command : new String[]{"unban"}) {
            getProxy().getPluginManager().registerCommand(this, new UnBanCommand(this, command));
        }
        for (String command : new String[]{"unmute"}) {
            getProxy().getPluginManager().registerCommand(this, new UnMuteCommand(this, command));
        }

        getProxy().getPluginManager().registerListener(this, new LoginListener(this));
        getProxy().getPluginManager().registerListener(this, new LogoutListener(this));
        getProxy().getPluginManager().registerListener(this, new ChatListener(this));

        setupChatLogBlockingQueue(this, "ChatLog");
}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        discord.shutdown();
        es.shutdown();
    }
}
