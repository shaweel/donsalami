package me.shaweel.donsalami.mixin.healing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.food.FoodData;

@Mixin(FoodData.class)
public class StopHunger {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void cancelTick(CallbackInfo callbackInfo) {
		callbackInfo.cancel();
	}
}
