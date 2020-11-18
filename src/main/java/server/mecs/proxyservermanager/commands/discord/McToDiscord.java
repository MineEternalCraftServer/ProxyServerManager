package server.mecs.proxyservermanager.commands.discord;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.threads.AccountUnSync;
import server.mecs.proxyservermanager.threads.CheckSynced;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class McToDiscord extends Command {

    ProxyServerManager plugin;

    public static Map<String, Integer> number = new HashMap<>();

    public McToDiscord(ProxyServerManager plugin, String name){
        super(name);
        this.plugin = plugin;
    }

    @Override public void execute(CommandSender sender, String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            if (args.length == 0) {
                sender.sendMessage(new ComponentBuilder("§b↓↓↓Discord Invitation Link↓↓↓").create());
                sender.sendMessage(new ComponentBuilder("§l==== https://discord.gg/FrSn838 ====").create());
                sender.sendMessage(new ComponentBuilder("§cDiscordで発言するためにはアカウントを同期する必要があります！\n" +
                        "§cYou will need to sync your account in order to speak on Discord!").create());
                return;
            }
            if (args[0] == "sync") {
                if (number.containsKey(sender)) {
                    sender.sendMessage(new ComponentBuilder("§cあなたはすでに連携を申請中です。\n§cしばらく待ってからもう一度試してください。\n" +
                            "§cYou are already in the process of requesting an account sync.\n§cPlease wait a while and try again.").create());
                    sender.sendMessage(new ComponentBuilder("§c/discord cancel").create());
                    return;
                }
                ProxiedPlayer player = (ProxiedPlayer) sender;
                UUID uuid = player.getUniqueId();
                if (plugin.MuteMap.containsKey(uuid)) {
                    sender.sendMessage(new ComponentBuilder("§cあなたは現在Muteされているためアカウント同期をできません。\n" +
                            "§cYou are currently Mute and therefore unable to sync your account.").create());
                    return;
                }
                if (!(CheckSynced.isSynced(plugin, sender.getName(), null))) {
                    sender.sendMessage(new ComponentBuilder("§cあなたはすでにアカウントを同期しています。\n" +
                            "§cYou have already synced your account.").create());
                    return;
                }
                number.put(sender.getName(), new Random().nextInt(9999 - 1000) + 1000);
                sender.sendMessage(new ComponentBuilder("§aあなたの認証IDは §b" + number.get(sender) + "§a です。\n" +
                        "§aこの認証IDをDiscordのMECS Bot#2386のDMに送信してください。\n" +
                        "§aYour authentication ID is §b" + number.get(sender) + "\n" +
                        "§aPlease send this authentication ID to the direct message on MECS Bot#2386.").create());
            }

            if (args[0] == "unsync") {
                if (CheckSynced.isSynced(plugin, sender.getName(), null)) {
                    AccountUnSync.AccountUnSync(plugin, sender.getName(), null);
                    sender.sendMessage(new ComponentBuilder("§aアカウント同期を解除しました。\n" +
                            "§aYour account has been unsynced.").create());
                }
                sender.sendMessage(new ComponentBuilder("§cあなたはアカウントを同期していません。\n" +
                        "§cYou have not synced your account.").create());
            }

            if (args[0] == "cancel") {
                number.remove(sender);
                sender.sendMessage(new ComponentBuilder("§aアカウント同期申請をキャンセルしました。\n" +
                        "§aCancelled your synchronization request.").create());
            }
        });
    }
}
