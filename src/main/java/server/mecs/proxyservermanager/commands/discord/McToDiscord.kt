package server.mecs.proxyservermanager.commands.discord

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import server.mecs.proxyservermanager.ProxyServerManager
import server.mecs.proxyservermanager.ProxyServerManager.Companion.discord
import server.mecs.proxyservermanager.ProxyServerManager.Companion.es
import server.mecs.proxyservermanager.database.DiscordData.isSynced
import server.mecs.proxyservermanager.database.DiscordData.unsyncAccount
import server.mecs.proxyservermanager.database.MySQLManager
import server.mecs.proxyservermanager.database.PlayerData.getIDfromMCID
import java.util.*
import java.util.concurrent.ExecutionException

class McToDiscord(var plugin: ProxyServerManager, name: String?) : Command(name) {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(*ComponentBuilder("""
                §b↓↓↓Discord Invitation Link↓↓↓
                §l==== https://discord.gg/FrSn838 ====
    §cDiscordで発言するためにはアカウントを同期する必要があります！
    §cYou will need to sync your account in order to speak on Discord!
    """.trimIndent()).create())
            return
        }

        if (args[0] == "sync") {
            es.execute {
                MySQLManager(plugin, "Discord syncAccount").use { mysql ->
                    if (number.containsKey(sender.name)) {
                        sender.sendMessage(*ComponentBuilder("""
    §cあなたはすでに連携を申請中です。
    §cしばらく待ってからもう一度試してください。
    §cYou are already in the process of requesting an account sync.
    §cPlease wait a while and try again.
    """.trimIndent()).create())
                        sender.sendMessage(*ComponentBuilder("§c/discord cancel").create())
                        return@execute
                    }
                    val player = sender as ProxiedPlayer
                    val uuid = player.uniqueId
                    try {
                        if (isSynced(mysql, sender.getName(), null)) {
                            sender.sendMessage(*ComponentBuilder("""
    §cあなたはすでにアカウントを同期しています。
    §cYou have already synced your account.
    """.trimIndent()).create())
                            return@execute
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }
                    var randomNumber = (Math.random() * 9999).toInt()
                    if (randomNumber <= 1000) {
                        randomNumber += 1000
                    }
                    number[sender.getName()] = randomNumber
                    sender.sendMessage(*ComponentBuilder("""
    §aあなたの認証IDは §b${number[sender.getName()]}§a です。
    §aこの認証IDをDiscordのMECS Bot#2386のDMに送信してください。
    §aYour authentication ID is §b${number[sender.getName()]}
    §aPlease send this authentication ID to the direct message on MECS Bot#2386.
    """.trimIndent()).create())
                }
            }
        }

        if (args[0] == "unsync") {
            es.execute {
                MySQLManager(plugin, "Discord unsyncAccount").use { mysql ->
                    try {
                        if (isSynced(mysql, sender.name, null)) {
                            ProxyServerManager.discord!!.removeRole(sender.name)
                            ProxyServerManager.discord!!.guild!!.modifyNickname(discord!!.guild?.getMemberById(getIDfromMCID(mysql, sender.name)!!)!!, "An_Unlinked_Player").queue()
                            try {
                                unsyncAccount(mysql, sender.name, null)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            sender.sendMessage(*ComponentBuilder("""
    §aアカウント同期を解除しました。
    §aYour account has been unsynced.
    """.trimIndent()).create())
                            return@execute
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }
                    sender.sendMessage(*ComponentBuilder("""
    §cあなたはアカウントを同期していません。
    §cYou have not synced your account.
    """.trimIndent()).create())
                }
            }
        }

        if (args[0] == "cancel") {
            number.remove(sender)
            sender.sendMessage(*ComponentBuilder("""
    §aアカウント同期申請をキャンセルしました。
    §aCancelled your synchronization request.
    """.trimIndent()).create())
        }
    }

    companion object {
        var number: MutableMap<String, Int> = HashMap()
    }
}