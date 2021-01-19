package server.mecs.proxyservermanager.commands.staffmessage

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.plugin.Command
import server.mecs.proxyservermanager.ProxyServerManager
import server.mecs.proxyservermanager.ProxyServerManager.Companion.es

class StaffMessage(private var plugin: ProxyServerManager, name: String?) : Command(name) {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (!sender.hasPermission("server.staffmessage")) {
            sender.sendMessage(*ComponentBuilder("§cYou do not have permission to use this command.").create())
            return
        }
        if (args.isEmpty()) {
            sender.sendMessage(*ComponentBuilder("§c/staff <message>").create())
            return
        }
        es.execute {
            val str = StringBuilder()
            for (i in args.indices) {
                str.append(args[i] + " ")
            }
            var message = str.toString().trim { it <= ' ' }
            message = ChatColor.translateAlternateColorCodes('&', message)
            sendStaffMessage(plugin, message)
        }
    }

    companion object {
        fun sendStaffMessage(plugin: ProxyServerManager, message: String) {
            for (players in plugin.proxy.players) {
                if (!players.hasPermission("server.staffmessage")) continue
                players.sendMessage(*ComponentBuilder("§b§l[STAFF]§r $message").create())
            }
            ProxyServerManager.discord!!.staffmessage("`" + ChatColor.stripColor(message) + "`")
        }
    }
}