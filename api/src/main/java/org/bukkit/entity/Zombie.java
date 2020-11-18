package org.bukkit.entity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Zombie.
 */
public interface Zombie extends Monster, Ageable {

    /**
     * Gets whether the zombie is a baby
     *
     * @return Whether the zombie is a baby
     * @deprecated see {@link Ageable#isAdult()}
     */
    @Deprecated
    public boolean isBaby();

    /**
     * Sets whether the zombie is a baby
     *
     * @param flag Whether the zombie is a baby
     * @deprecated see {@link Ageable#setBaby()} and {@link Ageable#setAdult()}
     */
    @Deprecated
    public void setBaby(boolean flag);

    /**
     * Gets whether the zombie is a villager
     *
     * @return Whether the zombie is a villager
     * @deprecated check if instanceof {@link ZombieVillager}.
     */
    @Deprecated
    public boolean isVillager();

    /**
     * @param flag flag
     * @deprecated must spawn {@link ZombieVillager}.
     */
    @Deprecated
    @Contract("_ -> fail")
    public void setVillager(boolean flag);

    /**
     * @param profession profession
     * @see ZombieVillager#getVillagerProfession()
     */
    @Deprecated
    @Contract("_ -> fail")
    public void setVillagerProfession(Villager.Profession profession);

    /**
     * @return profession
     * @see ZombieVillager#getVillagerProfession()
     */
    @Deprecated
    @Nullable
    @Contract("-> null")
    public Villager.Profession getVillagerProfession();

    /**
     * Get if this entity is in the process of converting to a Drowned as a
     * result of being underwater.
     *
     * @return conversion status
     */
    boolean isConverting();

    /**
     * Gets the amount of ticks until this entity will be converted to a Drowned
     * as a result of being underwater.
     *
     * When this reaches 0, the entity will be converted.
     *
     * @return conversion time
     * @throws IllegalStateException if {@link #isConverting()} is false.
     */
    int getConversionTime();

    /**
     * Sets the amount of ticks until this entity will be converted to a Drowned
     * as a result of being underwater.
     *
     * When this reaches 0, the entity will be converted. A value of less than 0
     * will stop the current conversion process without converting the current
     * entity.
     *
     * @param time new conversion time
     */
    void setConversionTime(int time);

    /**
     * Gets whether this zombie can break doors
     *
     * @return Whether this zombie can break doors
     */
    boolean canBreakDoors();

    /**
     * Sets whether this zombie can break doors
     *
     * This will be ignored if the entity is a Drowned. Will also stop the action if
     * the entity is currently breaking a door.
     *
     * @param flag Whether this zombie can break doors
     */
    void setCanBreakDoors(boolean flag);

    // Paper start
    /**
     * Check if zombie is drowning
     *
     * @return True if zombie conversion process has begun
     */
    boolean isDrowning();

    /**
     * Make zombie start drowning
     *
     * @param drownedConversionTime Amount of time until zombie converts from drowning
     *
     * @deprecated See {@link #setConversionTime(int)}
     */
    @Deprecated
    void startDrowning(int drownedConversionTime);

    /**
     * Stop a zombie from starting the drowning conversion process
     */
    void stopDrowning();

    /**
     * Set if zombie has its arms raised
     *
     * @param raised True to raise arms
     */
    void setArmsRaised(boolean raised);

    /**
     * Check if zombie has arms raised
     *
     * @return True if arms are raised
     */
    boolean isArmsRaised();

    /**
     * Check if this zombie will burn in the sunlight
     *
     * @return True if zombie will burn in sunlight
     */
    boolean shouldBurnInDay();

    /**
     * Set if this zombie should burn in the sunlight
     *
     * @param shouldBurnInDay True to burn in sunlight
     */
    void setShouldBurnInDay(boolean shouldBurnInDay);

    /**
     * Check if this zombie can break doors
     *
     * @return True if zombie can break doors
     */
    boolean canBreakDoors();

    /**
     * Sets if this zombie can break doors.
     * Check {@link #supportsBreakingDoors()} to see
     * if this zombie type will even be affected by using
     * this method.
     *
     * @param canBreakDoors True if zombie can break doors
     */
    void setCanBreakDoors(boolean canBreakDoors);

    /**
     * Checks if this zombie type supports breaking doors.
     * {@link Drowned} do not have support for breaking doors
     * so using {@link #setCanBreakDoors(boolean)} on them has
     * no effect.
     *
     * @return
     */
    boolean supportsBreakingDoors();
    // Paper end
}
