package me.aglerr.donations.objects;

import com.tcoded.folialib.impl.PlatformScheduler;
import me.aglerr.donations.DonationPlugin;
import me.aglerr.donations.managers.DonationGoal;
import me.aglerr.donations.utils.Events;
import me.aglerr.donations.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class QueueDonation {

    private final PlatformScheduler scheduler;
    @NotNull private final OfflinePlayer player;
    @NotNull private final Product product;

    public QueueDonation(@NotNull OfflinePlayer player, @NotNull Product product) {
        this.scheduler = DonationPlugin.scheduler();
        this.player = player;
        this.product = product;
    }

    @NotNull
    public OfflinePlayer getPlayer() {
        return player;
    }

    @NotNull
    public Product getProduct() {
        return product;
    }

    public void announceDonation() {
        scheduler.runNextTick(task -> Events.playAllEvents(this.getPlayer()));
        scheduler.runNextTick(task -> DonationGoal.handleDonation(this.getProduct()));
        scheduler.runAsync(task -> Utils.broadcastDonation(this));
    }
}
