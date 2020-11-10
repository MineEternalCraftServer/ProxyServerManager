package server.mecs.proxyservermanager.commands.punishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import server.mecs.proxyservermanager.ProxyServerManager;

public class UnBanCommand extends Command {

    ProxyServerManager plugin = null;

    public UnBanCommand(ProxyServerManager plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
