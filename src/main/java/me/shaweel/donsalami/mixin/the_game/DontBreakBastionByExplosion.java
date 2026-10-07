package me.shaweel.donsalami.mixin.the_game;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.donsalami.the_game.bastion.DontBreakBastion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
public class DontBreakBastionByExplosion {
	@Inject(at = @At("HEAD"), method = "onExplosionHit", cancellable = true)
	private void dontBreakBastion(
		BlockState state, ServerLevel level, BlockPos position, Explosion explosion, 
		BiConsumer<ItemStack, BlockPos> onHit, CallbackInfo callbackInfo
	) {
		if (DontBreakBastion.isProtected(level, position)) {
			callbackInfo.cancel();
		}
	}
}
