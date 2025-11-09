package me.aglerr.donations.managers;

import com.muhammaddaffa.mdlib.utils.Logger;
import me.aglerr.donations.DonationPlugin;
import me.aglerr.donations.objects.Product;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProductManager {

    private final Map<String, Product> productList = new HashMap<>();

    @Nullable
    public Product getProduct(String product) {
        return this.productList.get(product);
    }

    public void addProduct(String id, Product product) {
        this.productList.put(id, product);
    }

    public Set<String> getListOfProductName() {
        return this.productList.keySet();
    }

    public void reloadProduct() {
        // Clear all loaded product
        productList.clear();
        // Load them back up
        loadProduct();
    }

    public void loadProduct() {
        Logger.info("Starting to load all products...");
        // load all product inside product.yml
        this.loadProduct(DonationPlugin.PRODUCT_CONFIG.getConfig());
        // send log message
        Logger.info("Successfully loaded " + this.productList.size() + " product!");
    }

    public void loadProduct(FileConfiguration config) {
        ConfigurationSection productSection = config.getConfigurationSection("products");
        if (productSection == null) {
            Logger.info("&cNo products found in config!");
            return;
        }

        for (String id : productSection.getKeys(false)) {
            ConfigurationSection section = productSection.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            this.loadProduct(id, section);
        }
    }

    public void loadProduct(String id, ConfigurationSection section) {
        String displayName = section.getString("displayName");
        double price = section.getDouble("price");

        // Register all product inside
        Product product = new Product(id, displayName, price);
        this.productList.put(id, product);
        Logger.info("Successfully loaded '" +id+ "'");
    }

}
