package server.mecs.proxyservermanager

import net.md_5.bungee.api.plugin.Plugin
import server.mecs.proxyservermanager.commands.discord.McToDiscord
import server.mecs.proxyservermanager.commands.privatemessage.ReplyCommand
import server.mecs.proxyservermanager.commands.privatemessage.TellCommand
import server.mecs.proxyservermanager.commands.report.Report
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage
import server.mecs.proxyservermanager.database.MongoDBManager
import server.mecs.proxyservermanager.database.MySQLManager
import server.mecs.proxyservermanager.discord.Discord
import server.mecs.proxyservermanager.listeners.ChatListener
import server.mecs.proxyservermanager.listeners.LoginListener
import server.mecs.proxyservermanager.listeners.LogoutListener
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.concurrent.Executors

class ProxyServerManager : Plugin() {
    override fun onEnable() {
        // Plugin startup logic
        discord = Discord(this)
        for (command in arrayOf("tell", "msg", "message", "m", "w", "t")) {
            proxy.pluginManager.registerCommand(this, TellCommand(this, command))
        }
        for (command in arrayOf("reply", "r")) {
            proxy.pluginManager.registerCommand(this, ReplyCommand(this, command))
        }
        for (command in arrayOf("discord")) {
            proxy.pluginManager.registerCommand(this, McToDiscord(this, command))
        }
        for (command in arrayOf("staff")) {
            proxy.pluginManager.registerCommand(this, StaffMessage(this, command))
        }
        for (command in arrayOf("report")) {
            proxy.pluginManager.registerCommand(this, Report(this, command))
        }
        proxy.pluginManager.registerListener(this, LoginListener(this))
        proxy.pluginManager.registerListener(this, LogoutListener())
        proxy.pluginManager.registerListener(this, ChatListener(this))

        MySQLManager.setupBlockingQueue(this, "ProxyServerManager Queue")
        MongoDBManager.setupBlockingLoginQueue(this, "LoginLog")
        MongoDBManager.setupBlockingLogoutQueue(this, "LogoutLog")
        MongoDBManager.setupBlockingChatQueue(this, "ChatLog")


        loadConfig()
        discord!!.setup()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        discord!!.shutdown()
        es.shutdown()
    }

    fun loadConfig() {
        val config = ConfigFile(this).getConfig()
        discord?.token = config?.getString("Discord.Token")
        discord?.guildID = config?.getLong("Discord.Guild")!!
        discord?.receivereportChannelID = config.getLong("Discord.ReportChannel")
        discord?.staffmessageChannelID = config.getLong("Discord.StaffChannel")
    }

    companion object {
        var discord: Discord? = null
        val es = Executors.newCachedThreadPool()
    }
}