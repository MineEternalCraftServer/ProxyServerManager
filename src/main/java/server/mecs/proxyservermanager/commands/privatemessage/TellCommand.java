package server.mecs.proxyservermanager.commands.privatemessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;
import server.mecs.proxyservermanager.japanize.JapanizeType;
import server.mecs.proxyservermanager.japanize.Japanizer;
import server.mecs.proxyservermanager.utils.HistoryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TellCommand extends Command {
    static public ArrayList<UUID> viewlist;

    public ProxyServerManager plugin;
    public HistoryUtil util;

    public TellCommand(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
        viewlist = new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        // 引数が足らないので、Usageを表示して終了する。
        if (args.length <= 1) {
            sendMessage(sender, "§c/$name <player> <message> : Send private message.");
            return;
        }

        // 自分自身には送信できない。
        if (args[0].equals(sender.getName())) {
            sendMessage(sender, "§c自分自身にはプライベートメッセージを送信することができません。\n" +
                    "§cCannot send a private message to myself.");
            return;
        }

        // 送信先プレイヤーの取得。取得できないならエラーを表示して終了する。
        ProxiedPlayer reciever = plugin.getProxy().getPlayer(args[0]);
        if (reciever == null) {
            sendMessage(sender, "§cメッセージ送信先が見つかりません。\n" +
                    "§cThe destination for the message was not found.");
            return;
        }

        // 送信メッセージの作成
        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String message = str.toString().trim();

        // 送信
        sendPrivateMessage(sender, reciever, message);
    }

    HashMap<String, String> dic = new HashMap<>();

    /**
     * プライベートメッセージを送信する
     *
     * @param sender   送信者
     * @param reciever 受信者名
     * @param message  メッセージ
     */
    protected void sendPrivateMessage(CommandSender sender, ProxiedPlayer reciever, String message) {
        // Japanizeの付加
        String msg = ChatColor.translateAlternateColorCodes('&', message);
        String msgs = "";
        msgs = Japanizer.japanize(msg, JapanizeType.GOOGLE_IME, dic);
        if (!msgs.equalsIgnoreCase("")) {
            msg = msg + " §6(" + msgs + ")";
        }

        // フォーマットの適用
        String senderServer = "console";
        if (sender instanceof ProxiedPlayer) {
            senderServer = ((ProxiedPlayer) sender).getServer().getInfo().getName();
        }


        String endmsg = "§7[" + sender.getName() + "@" + senderServer + " > " + reciever.getName() + "@" + reciever.getServer().getInfo().getName() + "] §f" + msg;
        // メッセージ送信
        sendMessage(sender, endmsg);
        sendMessage(reciever, endmsg);
        //履歴をput
        util.putHistory(reciever.getName(), sender.getName());
        // コンソールに表示設定なら、コンソールに表示する
        plugin.getLogger().info(endmsg);
        // 権限もちで、かつviewモードがon さらに送信者でも受け取り者でもなければ 個チャを表示
        for (String server : ProxyServer.getInstance().getServers().keySet()) {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
            for (ProxiedPlayer players : info.getPlayers()) {
                if (players.hasPermission("bd.op")) {
                    if (viewlist.contains(players.getUniqueId())) {
                        if (!players.getName().equals(sender.getName()) && !players.getName().equals(reciever.getName())) {
                            players.sendMessage(TextComponent.fromLegacyText("§8[View]§r" + endmsg));
                        }
                    }
                }
            }
        }
    }


    /**
     * 指定した対象にメッセージを送信する
     *
     * @param reciever 送信先
     * @param message  メッセージ
     */
    protected void sendMessage(CommandSender reciever, String message) {
        if (message == null) return;
        reciever.sendMessage(TextComponent.fromLegacyText(message));
    }

}
