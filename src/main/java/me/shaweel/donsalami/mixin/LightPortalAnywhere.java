package me.shaweel.donsalami.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;

@Mixin(BaseFireBlock.class)
public class LightPortalAnywhere {
	@Inject(at = @At("HEAD"), method = "inPortalDimension", cancellable = true)
	private static void inPortalDimension(Level level, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(true);
	}
}
