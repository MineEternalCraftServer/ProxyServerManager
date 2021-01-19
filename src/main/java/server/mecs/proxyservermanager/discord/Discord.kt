package server.mecs.proxyservermanager.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import server.mecs.proxyservermanager.ProxyServerManager
import server.mecs.proxyservermanager.ProxyServerManager.Companion.es
import server.mecs.proxyservermanager.ProxyServerManager.Companion.getDate
import server.mecs.proxyservermanager.commands.discord.McToDiscord
import server.mecs.proxyservermanager.database.DiscordData.isSynced
import server.mecs.proxyservermanager.database.DiscordData.syncAccount
import server.mecs.proxyservermanager.database.DiscordData.unsyncAccount
import server.mecs.proxyservermanager.database.MySQLManager
import server.mecs.proxyservermanager.database.PlayerData.getIDfromMCID
import java.awt.Color
import java.util.concurrent.ExecutionException
import javax.security.auth.login.LoginException

class Discord(private val plugin: ProxyServerManager) : ListenerAdapter() {
    var jda: JDA? = null
    var token: String? = null
    var guildID = 0L
    var guild: Guild? = null
    var receivereportChannelID = 0L
    var receivereportChannel: TextChannel? = null
    var staffmessageChannelID = 0L
    var staffmessageChannel: TextChannel? = null
    var eb = EmbedBuilder()
    fun staffmessage(message: String?) {
        eb.setTitle("**StaffMessage**", null)
        eb.setColor(Color.RED)
        eb.setDescription(getDate().toString())
        eb.addField("**[Description]**", message, false)
        staffmessageChannel!!.sendMessage(eb.build()).queue()
        eb.clear()
    }

    fun receivereport(message: MessageEmbed?) {
        receivereportChannel!!.sendMessage(message!!).queue()
    }

    fun shutdown() {
        jda!!.shutdown()
        plugin.logger.info("discord shutdown")
    }

    fun setup() {
        plugin.logger.info("discord setup")
        if (token == null) {
            plugin.logger.info("Discord token is not initialized.")
            return
        }
        try {
            jda = JDABuilder
                    .createDefault(token)
                    .addEventListeners(this)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build()
            jda!!.awaitReady()
            guild = jda!!.getGuildById(guildID)
            receivereportChannel = guild!!.getTextChannelById(receivereportChannelID)
            staffmessageChannel = guild!!.getTextChannelById(staffmessageChannelID)
        } catch (e: LoginException) {
            e.printStackTrace()
            plugin.logger.info(e.localizedMessage)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            plugin.logger.info(e.localizedMessage)
        }
        plugin.logger.info("discord setup done!")
    }

    override fun onReady(e: ReadyEvent) {
        plugin.logger.info("discord bot ready")
    }

    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        es.execute { guild!!.modifyNickname(e.member, "An_Unlinked_Player").queue() }
    }

    override fun onGuildMemberRemove(e: GuildMemberRemoveEvent) {
        es.execute {
            MySQLManager(plugin, "Discord GuildMemberRemove").use { mysql ->
                val id = e.user.idLong
                try {
                    if (isSynced(mysql, null, id)) {
                        unsyncAccount(mysql, null, id)
                    }
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                } catch (ex: ExecutionException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        es.execute {
            if (e.author.id == jda!!.selfUser.id) {
                return@execute
            }
            if (e.channelType != ChannelType.TEXT) {
                return@execute
            }
            val emessage = e.message.contentDisplay
            val message = e.message.contentDisplay.replace("/report", "")
            val channel = e.channel
            val mention = "<@${e.author.id}>"
            if (emessage.indexOf("/report") == 0) {
                if (emessage.length <= 8) {
                    eb.setColor(Color(255, 0, 0))
                    eb.setDescription("""
    $mention
    十分な記述がありません。
    There is not enough description.
    /report <requirement>
    """.trimIndent())
                    channel.sendMessage(eb.build()).queue()
                    eb.clear()
                    return@execute
                }
                eb.setColor(Color(0, 255, 0))
                eb.setDescription("""
    ${mention}レポートを送信しました。。
    Your report was sent.
    """.trimIndent())
                channel.sendMessage(eb.build()).queue()
                eb.clear()
                eb.setTitle("**DiscordReport**", null)
                eb.setColor(Color(0, 255, 0))
                eb.setDescription(getDate().toString())
                eb.addField("**[Description]**", """**[Sender]** $mention

`$message`""", false)
                receivereport(eb.build())
                eb.clear()
            }
        }
    }

    override fun onPrivateMessageReceived(e: PrivateMessageReceivedEvent) {
        es.execute {
            if (e.message.author === jda!!.selfUser) return@execute
            try {
                e.message.contentDisplay.toInt()
            } catch (ex: NumberFormatException) {
                e.message.privateChannel.sendMessage("Failed to account sync.\nThe ID is not the correct number.").queue()
                return@execute
            }
            val player = getKeyByValue(McToDiscord.number, e.message.contentDisplay.toInt())!!
            val id = e.message.author.idLong
            val ID = e.message.contentDisplay.toInt()
            if (!McToDiscord.number.containsValue(ID)) {
                e.message.privateChannel.sendMessage("Failed to account sync.\nPlease try again.").queue()
                return@execute
            }
            MySQLManager(plugin, "Discord PrivateMessageReceived").use { mysql ->
                try {
                    if (isSynced(mysql, player, null)) {
                        e.message.privateChannel.sendMessage("Failed to account sync.\nApparently your account has already have sync.").queue()
                        return@execute
                    }
                } catch (ex: NullPointerException) {
                    e.message.privateChannel.sendMessage("Failed to account sync.\nPlease try again.").queue()
                    return@execute
                } catch (ex: InterruptedException) {
                    e.message.privateChannel.sendMessage("Failed to account sync.\nPlease try again.").queue()
                    return@execute
                } catch (ex: ExecutionException) {
                    e.message.privateChannel.sendMessage("Failed to account sync.\nPlease try again.").queue()
                    return@execute
                }
                try {
                    if (isSynced(mysql, null, e.message.author.idLong)) {
                        e.message.privateChannel.sendMessage("Failed to account sync.\nApparently your account has already have sync.").queue()
                        return@execute
                    }
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                } catch (ex: ExecutionException) {
                    ex.printStackTrace()
                }
                try {
                    syncAccount(mysql, player, id)
                    if (isSynced(mysql, null, id)) {
                        guild!!.modifyNickname(guild!!.getMemberById(id)!!, player).queue()
                        guild!!.addRoleToMember(id, guild!!.getRoleById(753582521685377034L)!!).queue()
                        McToDiscord.number.remove(player)
                        e.message.privateChannel.sendMessage("Successfully synced with your Minecraft account.").queue()
                    }
                    e.message.privateChannel.sendMessage("Failed to account sync.\nPlease report to the Staff Team.").queue()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    e.message.privateChannel.sendMessage("Failed to account sync.\nPlease report to the Staff Team.").queue()
                }
            }
        }
    }

    fun removeRole(mcid: String?) {
        es.execute {
            MySQLManager(plugin, "Discord removeRole").use { mysql ->
                guild?.removeRoleFromMember(getIDfromMCID(mysql, mcid!!)!!,
                        guild!!.getRoleById(753582521685377034L)!!)?.queue()
            }
        }
    }

    fun <T, E> getKeyByValue(map: Map<T, E>, value: E): T? {
        for ((key, value1) in map) {
            if (value == value1) {
                return key
            }
        }
        return null
    }
}