package me.shaweel.donsalami.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.donsalami.miscellaneous.Elytra;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

@Mixin(ServerPlayer.class)
public class ElytraDetectDeath {
	@Inject(method = "die", at = @At("HEAD"))
	private void onDie(DamageSource damageSource, CallbackInfo callbackInfo) {
		Elytra.reset();
	}
}
