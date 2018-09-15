package org.bukkit.event.weather;

import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Stores data for lightning striking
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean canceled;
    private final LightningStrike bolt;
    private final Cause cause;

    @Deprecated
    public LightningStrikeEvent(final World world, final LightningStrike bolt) {
        this(world, bolt, Cause.UNKNOWN);
    }

    public LightningStrikeEvent(final World world, final LightningStrike bolt, final Cause cause) {
        super(world);
        this.bolt = bolt;
        this.cause = cause;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    /**
     * Gets the bolt which is striking the earth.
     *
     * @return lightning entity
     */
    public LightningStrike getLightning() {
        return bolt;
    }

    /**
     * Gets the cause of this lightning strike.
     *
     * @return strike cause
     */
    public Cause getCause() {
        return cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Cause {
        /**
         * Triggered by the /summon command.
         */
        COMMAND,
        /**
         * Triggered by an enchanted trident.
         */
        TRIDENT,
        /**
         * Triggered by a skeleton horse trap.
         */
        TRAP,
        /**
         * Triggered by weather.
         */
        WEATHER,
        /**
         * Unknown trigger.
         */
        UNKNOWN;
    }
}
