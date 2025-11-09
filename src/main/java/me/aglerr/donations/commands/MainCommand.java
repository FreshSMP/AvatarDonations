package me.aglerr.donations.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import com.muhammaddaffa.mdlib.utils.Placeholder;
import me.aglerr.donations.DonationPlugin;
import me.aglerr.donations.managers.ProductManager;
import me.aglerr.donations.managers.QueueManager;
import me.aglerr.donations.objects.Product;
import org.bukkit.OfflinePlayer;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;

import java.util.List;

public class MainCommand {

    private final CommandAPICommand command;

    public MainCommand() {
        this.command = new CommandAPICommand("donations")
                .withPermission("donations.admin")
                .executes((sender, args) -> {
                    if (sender.hasPermission("donations.admin")) {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.help");
                    } else {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.noPermission");
                    }
                });

        // Register the SubCommand
        this.getSubCommandSend();
        this.getSubCommandReload();
        this.getSubCommandReset();

        // Register the command
        this.command.register(DonationPlugin.getInstance());
    }

    private void getSubCommandSend() {
        this.command.withSubcommand(new CommandAPICommand("send")
                        .withArguments(new PlayerProfileArgument("target"))
                        .withArguments(new StringArgument("product")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> DonationPlugin.getInstance().getProductManager()
                                        .getListOfProductName().toArray(String[]::new))))
                .withPermission("donations.admin")
                .executes((sender, args) -> {
                    if (!(sender.hasPermission("donations.admin"))) {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.noPermission");
                        return;
                    }

                    // Get the product manager
                    ProductManager productManager = DonationPlugin.getInstance().getProductManager();

                    // PlayerProfileArgument returns List<PlayerProfile>
                    @SuppressWarnings("unchecked")
                    List<PlayerProfile> profiles = (List<PlayerProfile>) args.get("target");
                    if (profiles == null || profiles.isEmpty()) {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.invalidTarget");
                        return;
                    }
                    if (profiles.size() > 1) {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.tooManyTargets");
                        return;
                    }

                    PlayerProfile pp = profiles.get(0);
                    OfflinePlayer target;
                    if (pp.getId() != null) {
                        target = Bukkit.getOfflinePlayer(pp.getId());
                    } else if (pp.getName() != null) {
                        target = Bukkit.getOfflinePlayer(pp.getName());
                    } else {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.invalidTarget");
                        return;
                    }

                    String string = (String) args.get("product");

                    // Get the product from the command argument
                    Product product = productManager.getProduct(string);
                    // Return if there is no product with that name
                    if (product == null){
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.invalidProduct");
                        return;
                    }
                    // If the product is existed, add the donation to the queue
                    // First, get the queue manager
                    QueueManager queueManager = DonationPlugin.getInstance().getQueueManager();
                    // Finally, add the donation to the queue
                    queueManager.addQueue(target, product);
                    // Send a success message
                    DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.performDonation", new Placeholder()
                            .add("{player}", target.getName()));
                }));
    }

    private void getSubCommandReload() {
        this.command.withSubcommand(new CommandAPICommand("reload")
                .withPermission("donations.admin")
                .executes((sender, args) -> {
                    if (!(sender.hasPermission("donations.admin"))) {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.noPermission");
                        return;
                    }
                    DonationPlugin.getInstance().reloadEverything();
                    DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.reload");
                }));
    }

    private void getSubCommandReset() {
        this.command.withSubcommand(new CommandAPICommand("reset")
                .withPermission("donations.admin")
                .executes((sender, args) -> {
                    if (!(sender.hasPermission("donations.admin"))) {
                        DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.noPermission");
                        return;
                    }
                    DonationPlugin.getInstance().resetDonation();
                    DonationPlugin.DEFAULT_CONFIG.sendMessage(sender, "messages.reset");
                }));
    }
}
