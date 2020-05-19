package me.toastedturtle.mobmoney;

import me.toastedturtle.mobmoney.commands.MobMoneyCmd;
import me.toastedturtle.mobmoney.listeners.onMobDeathListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MobMoney extends JavaPlugin {

    public ConsoleCommandSender consoleSender = getServer().getConsoleSender();

    private static final Logger log = Logger.getLogger("Minecraft");
    public Economy econ = null;

    private onMobDeathListener mobDeathListener;
    private MobMoneyCmd mobMoneyCmd;

    public FileConfiguration config;

    public File dataFile;
    public FileConfiguration dataConfig;

    public void onEnable() {

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.saveDefaultConfig();
        config = getConfig();

        createCustomFile();

        mobMoneyCmd = new MobMoneyCmd(this);
        mobDeathListener = new onMobDeathListener(this);
        this.getCommand("mobmoney").setExecutor(mobMoneyCmd);
        getServer().getPluginManager().registerEvents(mobDeathListener, this);

        int pluginId = 7535;
        Metrics metrics = new Metrics(this, pluginId);

        consoleSender.sendMessage(ChatColor.GREEN + "MobMoney successfully enabled!");
        consoleSender.sendMessage(ChatColor.GREEN + "Made by - " + getDescription().getAuthors());
        consoleSender.sendMessage(ChatColor.GREEN + "Running version - " + getDescription().getVersion());
        consoleSender.sendMessage(ChatColor.GREEN + "Any issues? Add me on discord - heath#5408");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void rldConfig() {
        reloadConfig();
        saveConfig();
        config = getConfig();
    }

    private void createCustomFile() {
        dataFile = new File(getDataFolder()+"/data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveCustomFile(FileConfiguration ymlConfig, File ymlFile) {
        try {
            ymlConfig.save(ymlFile);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void onDisable() {
        saveConfig();
        saveCustomFile(dataConfig, dataFile);
    }
}
