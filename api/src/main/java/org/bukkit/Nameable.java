package org.bukkit;

import org.jetbrains.annotations.Nullable;

public interface Nameable {

    // Paper start
    /**
     * Gets the custom name.
     *
     * <p>This value has no effect on players, they will always use their real name.</p>
     *
     * @return the custom name
     */
    @Nullable net.kyori.adventure.text.Component customName();

    /**
     * Sets the custom name.
     *
     * <p>This name will be used in death messages and can be sent to the client as a nameplate over the mob.</p>
     *
     * <p>Setting the name to {@code null} will clear it.</p>
     *
     * <p>This value has no effect on players, they will always use their real name.</p>
     *
     * @param customName the custom name to set
     */
    void customName(final @Nullable net.kyori.adventure.text.Component customName);
    // Paper end

    /**
     * Gets the custom name on a mob or block. If there is no name this method
     * will return null.
     * <p>
     * This value has no effect on players, they will always use their real
     * name.
     *
     * @deprecated in favour of {@link #customName()}
     * @return name of the mob/block or null
     */
    @Deprecated // Paper
    @Nullable
    public String getCustomName();

    /**
     * Sets a custom name on a mob or block. This name will be used in death
     * messages and can be sent to the client as a nameplate over the mob.
     * <p>
     * Setting the name to null or an empty string will clear it.
     * <p>
     * This value has no effect on players, they will always use their real
     * name.
     *
     * @deprecated in favour of {@link #customName(net.kyori.adventure.text.Component)}
     * @param name the name to set
     */
    @Deprecated // Paper
    public void setCustomName(@Nullable String name);
}
