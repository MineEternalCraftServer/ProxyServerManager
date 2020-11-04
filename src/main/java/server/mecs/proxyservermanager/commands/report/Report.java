package server.mecs.proxyservermanager.commands.report;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report extends Command {

    public ProxyServerManager plugin;

    public Report(ProxyServerManager plugin, String name){
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0){
            sender.sendMessage(new ComponentBuilder("").create());
            sender.sendMessage(new ComponentBuilder("§c/report <requirement>").create());
            sender.sendMessage(new ComponentBuilder("").create());
            return;
        }

        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String message = str.toString().trim();

        Date now = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        plugin.discord.eb.setTitle("**新規Serverレポートが届きました！**", null);
        plugin.discord.eb.setColor(new Color(0, 255, 255));
        plugin.discord.eb.setDescription(format.format(now));
        plugin.discord.eb.addField("**[Description]**", "**[Sender]** `" + sender + "`\n \n`" + message + "`", false);

        plugin.discord.receivereport(plugin.discord.eb.build());

        plugin.discord.eb.clear();

        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "staff " + message);

        sender.sendMessage(new ComponentBuilder("§aレポートを送信しました。\n§aあなたのレポートを受け取り早急に対応したします。" +
                "\n§aThe report was sent.\n§aWe will take your report and respond to it as soon as possible.").create());
    }
}
