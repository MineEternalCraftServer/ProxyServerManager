package server.mecs.proxyservermanager.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.commands.discord.McToDiscord;
import server.mecs.proxyservermanager.threads.AccountSync;
import server.mecs.proxyservermanager.threads.CheckSynced;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Discord extends ListenerAdapter {
    public ProxyServerManager plugin = null;

    JDA jda;

    public String token = null;

    public Guild guild = null;
    public Long guildID = null;

    public Long receivereportChannelID = null;
    public TextChannel receivereportChannel = null;

    public Long staffmessageChannelID = null;
    public TextChannel staffmessageChannel = null;

    public EmbedBuilder eb;

    public Long botID = 749010040199053434L;

    Date now = null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void staffmessage(String message){
        eb.setTitle("**StaffMessage**", null);

        eb.setColor(Color.RED);

        eb.setDescription(format.format(now));

        eb.addField("**[Description]**", message, false);

        staffmessageChannel.sendMessage(eb.build()).queue();

        eb.clear();
    }

    public void receivereport(MessageEmbed message) {
        receivereportChannel.sendMessage(message).queue();
    }

    public void shutdown(){
        jda.shutdown();
        plugin.getLogger().info("discord shutdown");
    }

    public void setup(){
        plugin.getLogger().info("discord setup");

        if (token == null){
            plugin.getLogger().info("Discord token is not initialized.");
            return;
        }

        try {
            jda = JDABuilder
                    .createDefault(token)
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();

            guild = jda.getGuildById(guildID);
            receivereportChannel = guild.getTextChannelById(guildID);
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            plugin.getLogger().info(e.getLocalizedMessage());
        }
        plugin.getLogger().info("discord setup done!");
    }

    @Override public void onReady(ReadyEvent e){
        plugin.getLogger().info("discord bot ready");
    }

    @Override public void onMessageReceived(MessageReceivedEvent e){
        if (e.getAuthor().getId() == jda.getSelfUser().getId()){
            return;
        }
        if (e.getChannelType() != ChannelType.TEXT){
            return;
        }

        String report = e.getMessage().getContentDisplay();
        String message = e.getMessage().getContentDisplay().replace("/report", "");
        MessageChannel channel = e.getChannel();

        if (report.indexOf("/report") == 0) {
            if (e.getMessage().getContentRaw() == "/report") {
                eb.setColor(Color.GREEN);
                eb.setDescription("<@" + e.getAuthor().getId() + ">\n十分な記述がありません。\nThere is not enough description.\n/report <requirement>");
                channel.sendMessage(eb.build()).queue();
                eb.clear();
                return;
            }

            eb.setColor(Color.GREEN);
            eb.setDescription("<@" + e.getAuthor().getId() + ">レポートを送信しました。\nThe report was sent.");
            channel.sendMessage(eb.build()).queue();
            eb.clear();

            eb.setTitle("**DiscordReport**", null);
            eb.setColor(Color.GREEN);
            eb.setDescription(format.format(now));
            eb.addField("**[Description]**", "**[Sender]** <@" + e.getAuthor().getId() + ">\n \n`" + message + "`", false);

            receivereport(eb.build());

            eb.clear();
        }
    }

    @Override public void onPrivateMessageReceived(PrivateMessageReceivedEvent e){
        if (e.getMessage().getAuthor().getIdLong() == botID)return;
        try {
            Integer.parseInt(e.getMessage().getContentDisplay());
        }catch (NumberFormatException ex){
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nThe ID is not the correct number.").queue();
            return;
        }

        ProxiedPlayer player = getKeyByValue(McToDiscord.number, Integer.parseInt(e.getMessage().getContentDisplay()));
        Long id =  e.getMessage().getAuthor().getIdLong();
        Integer ID = Integer.parseInt(e.getMessage().getContentDisplay());

        if (!(McToDiscord.number.containsValue(ID))){
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nPlease try again.").queue();
            return;
        }

        try{
            if (CheckSynced.isSynced(plugin, player.getName())){
                e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nApparently your account has already have sync.").queue();
                return;
            }
        }catch (NullPointerException ex){
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nPlease try again.").queue();
            return;
        }

        if (CheckSynced.isSynced(plugin, e.getMessage().getAuthor().getIdLong())){
            e.getMessage().getPrivateChannel().sendMessage("Failed to account sync.\nApparently your account has already have sync.").queue();
            return;
        }

        try{
            AccountSync.AccountSync(plugin, player, id);
            guild.addRoleToMember(id, guild.getRoleById(753582521685377034L));
            guild.modifyNickname(guild.getMemberById(id), player.getName());
            e.getMessage().getPrivateChannel().sendMessage("Successfully synced with your Minecraft account.").queue();
        }catch (Exception ex){
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

}
