package me.shaweel.donsalami.mixin.patches;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.level.Level;

@Mixin(EnderpearlItem.class)
public class DisablePearlsBeforeMalgosha {
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	private void preventUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
		//TODO check for Malgosha completion after finishing the Malgosha bossfight
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
				Component.literal("You may not use ender pearls before beating the first boss fight.").withColor(TextColor.RED)
			));
		}

		callbackInfoReturnable.setReturnValue(InteractionResult.SUCCESS);
	}
}
