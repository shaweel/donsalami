package me.shaweel.donsalami.mixin.miscellaneous;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.donsalami.splits.SplitImplementation;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

@Mixin(ServerPlayer.class)
public class ResetSplitOnDeath {
	@Inject(method = "die", at = @At("TAIL"))
	private void resetSplit(DamageSource damageSource, CallbackInfo CallbackInfo) {
		ServerPlayer player = (ServerPlayer)(Object)this;

		SplitImplementation.resetSplit(player.level().getServer());
	}
}
