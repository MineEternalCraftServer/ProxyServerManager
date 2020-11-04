package server.mecs.proxyservermanager.commands.discord;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class McToDiscord extends Command {

    ProxyServerManager plugin;

    public static Map<ProxiedPlayer, Integer> number = new HashMap<>();

    public McToDiscord(ProxyServerManager plugin, String name){
        super(name);
        this.plugin = plugin;
    }

    @Override public void execute(CommandSender sender, String[] args) {
        if (args.length == 0){
            sender.sendMessage(new ComponentBuilder("§b↓↓↓Discord Invitation Link↓↓↓").create());
            sender.sendMessage(new ComponentBuilder("§l==== https://discord.gg/FrSn838 ====").create());
            sender.sendMessage(new ComponentBuilder("§cDiscordで発言するためにはアカウントをリンクする必要があります！\n" +
                    "§cYou will need to sync your account in order to speak on Discord!").create());
            return;
        }
        switch (args[0]){
            case "sync":
                if (number.containsKey(sender)){
                    sender.sendMessage(new ComponentBuilder("§cあなたはすでに連携を申請中です。\n§cしばらく待ってからもう一度試してください。\n" +
                            "§cYou are already in the process of requesting an account sync.\n§cPlease wait a while and try again.").create());
                    sender.sendMessage(new ComponentBuilder("§c/discord cancel").create());
                    return;
                }
                ProxiedPlayer player = (ProxiedPlayer) sender;
                UUID uuid = player.getUniqueId();
                if (plugin.MuteMap.containsKey(uuid)){
                    sender.sendMessage(new ComponentBuilder("§cあなたは現在Muteされているためアカウント同期をできません。\n" +
                            "§cYou are currently Mute and therefore unable to sync your account.").create());
                    return;
                }

        }
    }
}
