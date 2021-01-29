package org.bukkit.craftbukkit.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.apache.commons.lang.Validate;

public class CraftMerchantCustom extends CraftMerchant {

    @Deprecated // Paper - Adventure
    public CraftMerchantCustom(String title) {
        super(new MinecraftMerchant(title));
        this.getMerchant().craftMerchant = this;
    }
    // Paper start
    public CraftMerchantCustom(net.kyori.adventure.text.Component title) {
        super(new MinecraftMerchant(title));
        getMerchant().craftMerchant = this;
    }
    // Paper end

    @Override
    public String toString() {
        return "CraftMerchantCustom";
    }

    @Override
    public MinecraftMerchant getMerchant() {
        return (MinecraftMerchant) super.getMerchant();
    }

    public static class MinecraftMerchant implements Merchant {

        private final Component title;
        private final MerchantOffers trades = new MerchantOffers();
        private Player tradingPlayer;
        private Level tradingWorld;
        protected CraftMerchant craftMerchant;

        @Deprecated // Paper - Adventure
        public MinecraftMerchant(String title) {
            Validate.notNull(title, "Title cannot be null");
            this.title = Component.literal(title);
        }
        // Paper start
        public MinecraftMerchant(net.kyori.adventure.text.Component title) {
            Validate.notNull(title, "Title cannot be null");
            this.title = io.papermc.paper.adventure.PaperAdventure.asVanilla(title);
        }
        // Paper end

        @Override
        public CraftMerchant getCraftMerchant() {
            return this.craftMerchant;
        }

        @Override
        public void setTradingPlayer(Player customer) {
            this.tradingPlayer = customer;
            if (customer != null) {
                this.tradingWorld = customer.level;
            }
        }

        @Override
        public Player getTradingPlayer() {
            return this.tradingPlayer;
        }

        @Override
        public MerchantOffers getOffers() {
            return this.trades;
        }

        @Override
        public void notifyTrade(MerchantOffer offer) {
            // increase recipe's uses
            offer.increaseUses();
        }

        @Override
        public void notifyTradeUpdated(ItemStack stack) {
        }

        public Component getScoreboardDisplayName() {
            return this.title;
        }

        @Override
        public int getVillagerXp() {
            return 0; // xp
        }

        @Override
        public void overrideXp(int experience) {
        }

        @Override
        public boolean showProgressBar() {
            return false; // is-regular-villager flag (hides some gui elements: xp bar, name suffix)
        }

        @Override
        public SoundEvent getNotifyTradeSound() {
            return SoundEvents.VILLAGER_YES;
        }

        @Override
        public void overrideOffers(MerchantOffers offers) {
        }

        @Override
        public boolean isClientSide() {
            return false;
        }
    }
}
