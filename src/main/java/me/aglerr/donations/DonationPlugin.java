package me.aglerr.donations;

import com.muhammaddaffa.mdlib.MDLib;
import com.muhammaddaffa.mdlib.utils.Config;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import me.aglerr.donations.commands.MainCommand;
import me.aglerr.donations.managers.DependencyManager;
import me.aglerr.donations.managers.DonationGoal;
import me.aglerr.donations.managers.ProductManager;
import me.aglerr.donations.managers.QueueManager;
import me.aglerr.donations.metrics.Metrics;
import me.aglerr.donations.utils.Utils;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DonationPlugin extends JavaPlugin {

    private static PlatformScheduler scheduler;

    public static Config DEFAULT_CONFIG, PRODUCT_CONFIG, DATA;

    public static boolean HEX_AVAILABLE = false;

    private final ProductManager productManager = new ProductManager();
    private QueueManager queueManager;

    private static DonationPlugin instance;

    public static PlatformScheduler scheduler() {
        return scheduler;
    }

    @Override
    public void onLoad() {
        MDLib.inject(this);
        CommandAPI.onLoad(new CommandAPIPaperConfig(this).silentLogs(false));
    }

    @Override
    public void onEnable() {
        // Initialize the instance
        instance = this;
        // Initialize Folia schedule handling
        FoliaLib foliaLib = new FoliaLib(this);
        scheduler = foliaLib.getScheduler();
        // Injecting the libs
        MDLib.onEnable(this);
        // Instantiate CommandAPI
        CommandAPI.onEnable();
        // Send startup logo
        Utils.sendStartupLogo();
        // Initialize the queue manager
        queueManager = new QueueManager();
        // Check the dependencies
        DependencyManager.checkDependencies();
        // Initialize all config
        InitializeConfig();
        // Initialize all config value
        ConfigValue.initialize();
        // Load all products
        productManager.loadProduct();
        // Load the donation goal
        DonationGoal.onLoad();
        // Register the main command
        new MainCommand();
        // Register the command
        new MainCommand();
        // bStats metrics
        new Metrics(this, 10310);
        HEX_AVAILABLE = Bukkit.getVersion().contains("1.16") ||
                Bukkit.getVersion().contains("1.17") ||
                Bukkit.getVersion().contains("1.18") ||
                Bukkit.getVersion().contains("1.19") ||
                Bukkit.getVersion().contains("1.20") ||
                Bukkit.getVersion().contains("1.21");
    }

    @Override
    public void onDisable() {
        MDLib.shutdown();
        CommandAPI.onDisable();
        DonationGoal.onSave();
    }

    private void InitializeConfig() {
        DEFAULT_CONFIG = new Config("config.yml", null, true);
        DEFAULT_CONFIG.setShouldUpdate(true);
        PRODUCT_CONFIG = new Config("product.yml", null, true);
        DATA = new Config("data.yml", null, false);
        // Update the Config if there's any changes
        Config.updateConfigs();
        Config.reload();
    }

    public void reloadEverything() {
        // Reload all configs
        DEFAULT_CONFIG.reloadConfig();
        PRODUCT_CONFIG.reloadConfig();
        // Re-initialize the config value
        ConfigValue.initialize();
        // Reload all products
        productManager.reloadProduct();
        // Reload the donation goal
        DonationGoal.reloadDonationGoal();
    }

    public void resetDonation() {
        DonationGoal.reset();
    }

    public static DonationPlugin getInstance() {
        return instance;
    }

    public static SkinsRestorer getSkinsApi() {
        return SkinsRestorerProvider.get();
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
