package me.shaweel.donsalami.mixin.miscellaneous;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.donsalami.worldData.SoldPotatoes;
import me.shaweel.donsalami.worldData.SoldSeeds;
import me.shaweel.donsalami.worldData.SoldWheat;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

@Mixin(AbstractVillager.class)
public class VillagerTradeDetection {
	@Inject(at = @At("HEAD"), method = "notifyTrade")
	private void notifyTrade(MerchantOffer offer, CallbackInfo callbackInfo) {
		AbstractVillager villager = (AbstractVillager)(Object)this;

		if (offer.getItemCostA().item().value().equals(Items.WHEAT)) {
			SoldWheat.getData(villager.level().getServer()).set(true);
		} else if (offer.getItemCostA().item().value().equals(Items.WHEAT_SEEDS)) {
			SoldSeeds.getData(villager.level().getServer()).set(true);
		} else if (offer.getItemCostA().item().value().equals(Items.BAKED_POTATO)) {
			SoldPotatoes.getData(villager.level().getServer()).set(true);
		}
	}
}
