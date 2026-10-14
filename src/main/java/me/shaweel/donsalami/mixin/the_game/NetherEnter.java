package me.shaweel.donsalami.mixin.the_game;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.donsalami.splits.SplitImplementation;
import me.shaweel.donsalami.worldData.NetherEnters;

@Mixin(NetherPortalBlock.class)
public class NetherEnter {
	@Inject(at = @At("HEAD"), method = "getPortalDestination", cancellable = true)
	private void netherPortal(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos, CallbackInfoReturnable<ServerPlayer> callbackInfoReturnable) {		
		MinecraftServer server = currentLevel.getServer();
		if (!(entity instanceof ServerPlayer)) return;

		NetherEnters netherEnters = NetherEnters.getData(server);
		int oldNetherEnters = netherEnters.get();

		netherEnters.set(netherEnters.get() + 1);

		if (oldNetherEnters > 6) { return; }
		SplitImplementation.advanceSplit(server);

		callbackInfoReturnable.setReturnValue(null);
	}
}