package me.toastedturtle.mobmoney.commands;

import me.toastedturtle.mobmoney.MobMoney;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobMoneyCmd implements CommandExecutor {

    private MobMoney plugin;

    public MobMoneyCmd(MobMoney mobMoney) {
        this.plugin = mobMoney;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Mob Money - created by toastedturtle!");
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("mobmoney.reload")) {
            plugin.rldConfig();
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded config!");
            return true;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();

            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("disable") && player.hasPermission("mobmoney.disable")) {
                    if(plugin.dataConfig.contains(uuid)) {
                        player.sendMessage(ChatColor.RED + "Already disabled!");
                    }
                    else {
                        plugin.dataConfig.createSection(uuid);
                        plugin.saveCustomFile(plugin.dataConfig, plugin.dataFile);
                        player.sendMessage(ChatColor.GREEN + "Disabled reward messages!");
                    }
                }
                else if(args[0].equalsIgnoreCase("enable") && player.hasPermission("mobmoney.enable")) {
                    if(plugin.dataConfig.contains(uuid)) {
                        plugin.dataConfig.set(uuid, null);
                        plugin.saveCustomFile(plugin.dataConfig, plugin.dataFile);
                        player.sendMessage(ChatColor.GREEN + "Enabled reward messages!");
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Already enabled!");
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /mobmoney [reload|enable|disable]");
                }
            }
        }
        return true;
    }
}
