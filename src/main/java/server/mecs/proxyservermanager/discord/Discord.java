package server.mecs.proxyservermanager.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.md_5.bungee.config.Configuration;
import server.mecs.proxyservermanager.ConfigFile;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.commands.discord.McToDiscord;
import server.mecs.proxyservermanager.threads.AccountSync;
import server.mecs.proxyservermanager.threads.AccountUnSync;
import server.mecs.proxyservermanager.threads.CheckSynced;
import server.mecs.proxyservermanager.threads.getIDfromMCID;
import server.mecs.proxyservermanager.utils.getDate;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Discord extends ListenerAdapter {
    public ProxyServerManager plugin = null;

    JDA jda;

    public String token = null;

    public Long guildID = 0L;
    public Guild guild = null;

    public Long receivereportChannelID = 0L;
    public TextChannel receivereportChannel = null;

    public Long staffmessageChannelID = 0L;
    public TextChannel staffmessageChannel = null;

    public EmbedBuilder eb = new EmbedBuilder();

    String date = getDate.getDate();

    public Discord(ProxyServerManager plugin) {
        Configuration config = new ConfigFile(plugin).getConfig();
        token = config.getString("Discord.Token");
        guildID = config.getLong("Discord.Guild");
        receivereportChannelID = config.getLong("Discord.ReportChannel");
        staffmessageChannelID = config.getLong("Discord.StaffChannel");

        this.plugin = plugin;
        setup();
    }

    public void staffmessage(String message) {
        eb.setTitle("**StaffMessage**", null);

        eb.setColor(Color.RED);

        eb.setDescription(date);

        eb.addField("**[Description]**", message, false);

        staffmessageChannel.sendMessage(eb.build()).queue();

        eb.clear();
    }

    public void receivereport(MessageEmbed message) {
        receivereportChannel.sendMessage(message).queue();
    }

    public void shutdown() {
        jda.shutdown();
        plugin.getLogger().info("discord shutdown");
    }

    public void setup() {
        plugin.getLogger().info("discord setup");

        if (token == null) {
            plugin.getLogger().info("Discord token is not initialized.");
            return;
        }

        try {

            jda = JDABuilder
                    .createDefault(token)
                    .addEventListeners(this)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing("MECS"))
                    .build();
            jda.awaitReady();

            guild = jda.getGuildById(guildID);
            receivereportChannel = guild.getTextChannelById(receivereportChannelID);
            staffmessageChannel = guild.getTextChannelById(staffmessageChannelID);
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            plugin.getLogger().info(e.getLocalizedMessage());
        }
        plugin.getLogger().info("discord setup done!");
    }

    @Override
    public void onReady(ReadyEvent e) {
        plugin.getLogger().info("discord bot ready");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        guild.modifyNickname(e.getMember(), "An_Unlinked_Player").queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        try {
            if (CheckSynced.isSynced(plugin, null, e.getUser().getIdLong())) {
                AccountUnSync.AccountUnSync(plugin, null, e.getUser().getIdLong());
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().getId().equals(jda.getSelfUser().getId())) {
            return;
        }
        if (e.getChannelType() != ChannelType.TEXT) {
            return;
        }

        String emessage = e.getMessage().getContentDisplay();
        String message = e.getMessage().getContentDisplay().replace("/report", "");
        MessageChannel channel = e.getChannel();

        if (emessage.indexOf("/report") == 0) {
            if (emessage.length() <= 8) {
                eb.setColor(new Color(255, 0, 0));
                eb.setDescription("<@" + e.getAuthor().getId() + ">\n十分な記述がありません。\nThere is not enough description.\n/report <requirement>");
                channel.sendMessage(eb.build()).queue();
                eb.clear();
                return;
            }

            eb.setColor(new Color(0, 255, 0));
            eb.setDescription("<@" + e.getAuthor().getId() + ">レポートを送信しました。\nThe report was sent.");
            channel.sendMessage(eb.build()).queue();
            eb.clear();

            eb.setTitle("**DiscordReport**", null);
            eb.setColor(new Color(0, 255, 0));
            eb.setDescription(date);
            eb.addField("**[Description]**", "**[Sender]** <@" + e.getAuthor().getId() + ">\n \n`" + message + "`", false);
            receivereport(eb.build());
            eb.clear();
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getMessage().getAuthor() == jda.getSelfUser()) return;
        try {
            Integer.parseInt(e.getMessage().getContentDisplay());
        } catch (NumberFormatException ex) {
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nThe ID is not the correct number.").queue();
            return;
        }

        String player = getKeyByValue(McToDiscord.number, Integer.parseInt(e.getMessage().getContentDisplay()));
        Long id = e.getMessage().getAuthor().getIdLong();
        Integer ID = Integer.parseInt(e.getMessage().getContentDisplay());

        if (!(McToDiscord.number.containsValue(ID))) {
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nPlease try again.").queue();
            return;
        }

        try {
            if (CheckSynced.isSynced(plugin, player, null)) {
                e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nApparently your account has already have sync.").queue();
                return;
            }
        } catch (NullPointerException | InterruptedException | ExecutionException ex) {
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nPlease try again.").queue();
            return;
        }

        try {
            if (CheckSynced.isSynced(plugin, null, e.getMessage().getAuthor().getIdLong())) {
                e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nApparently your account has already have sync.").queue();
                return;
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        try {
            AccountSync.AccountSync(plugin, player, id);
            guild.modifyNickname(guild.getMemberById(id), player).queue();
            guild.addRoleToMember(id, guild.getRoleById(753582521685377034L)).queue();
            McToDiscord.number.remove(player);
            e.getMessage().getPrivateChannel().sendMessage("Successfully synced with your Minecraft account.").queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nPlease report to the Staff Team.").queue();
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void removeRole(String mcid) throws InterruptedException {
        guild.removeRoleFromMember(getIDfromMCID.getIDfromMCID(plugin, mcid), guild.getRoleById(753582521685377034L)).queue();
    }
}
