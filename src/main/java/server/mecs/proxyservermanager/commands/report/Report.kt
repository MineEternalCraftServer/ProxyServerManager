package server.mecs.proxyservermanager.commands.report

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import server.mecs.proxyservermanager.ProxyServerManager
import server.mecs.proxyservermanager.ProxyServerManager.Companion.discord
import server.mecs.proxyservermanager.ProxyServerManager.Companion.es
import server.mecs.proxyservermanager.ProxyServerManager.Companion.getDate
import server.mecs.proxyservermanager.commands.staffmessage.StaffMessage
import java.awt.Color
import java.util.*
import kotlin.math.floor

class Report(var plugin: ProxyServerManager, name: String?) : Command(name) {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is ProxiedPlayer) return
        if (args.isEmpty()) {
            sender.sendMessage(*ComponentBuilder("").create())
            sender.sendMessage(*ComponentBuilder("§c/report <requirement>").create())
            sender.sendMessage(*ComponentBuilder("").create())
            return
        }
        val uuid = sender.uniqueId
        if (CoolTime.containsKey(uuid)) {
            if (CoolTime[uuid]!! + 1000 * 30 <= System.currentTimeMillis()) {
                CoolTime.remove(uuid)
            } else {
                val timeleft = CoolTime[uuid]!! + 1000 * 30 - System.currentTimeMillis().toDouble()
                val timeleftformat = floor(timeleft / 1000).toInt()
                sender.sendMessage(*ComponentBuilder("§cYour report cooldown has $timeleftformat§c seconds left.").create())
                return
            }
        }
        CoolTime[uuid] = System.currentTimeMillis()
        es.execute {
            val str = StringBuilder()
            for (i in args.indices) {
                str.append(args[i] + " ")
            }
            val message = str.toString().trim { it <= ' ' }
            val date = getDate()
            discord?.eb?.setTitle("**ServerReport**", null)
            discord?.eb?.setColor(Color(0, 255, 255))
            discord?.eb?.setDescription(date.toString())
            discord?.eb?.addField("**[Description]**", "**[Sender]** `$sender`\n \n`$message`", false)
            discord?.receivereport(ProxyServerManager.discord!!.eb.build())
            discord?.eb?.clear()
            StaffMessage.sendStaffMessage(plugin, """
     §c§l[REPORT]
     §bReported by ${sender.getName()}.
     §6Description: $message
     """.trimIndent())
            sender.sendMessage(*ComponentBuilder("""
    §aレポートを送信しました。
    §aあなたのレポートを受け取り早急に対応したします。
    §aThe report was sent.
    §aWe will take your report and respond to it as soon as possible.
    """.trimIndent()).create())
        }
    }

    companion object {
        var CoolTime = HashMap<UUID, Long>()
    }
}