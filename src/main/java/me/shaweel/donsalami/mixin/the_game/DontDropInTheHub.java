package me.shaweel.donsalami.mixin.the_game;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.donsalami.recoursekeys.Dimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(ServerPlayer.class)
public class DontDropInTheHub {
	@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
	private void preventDrop(ItemStack itemStack, boolean thrownFromHand, Prediction prediction, CallbackInfoReturnable<ItemEntity> callbackInfoReturnable) {
		ServerPlayer player = (ServerPlayer)(Object)this;

		if (!player.level().dimension().equals(Dimensions.THE_HUB)) return;

		boolean internalDrop = StackWalker.getInstance().walk(stream -> stream.anyMatch(frame ->
			frame.getMethodName().equals("quickMoveStack")
			|| frame.getMethodName().equals("clearContainer")
		));

		if (internalDrop) return;

		player.sendSystemMessage(
			Component.literal("You can't do that here! ").withColor(TextColor.RED).append(Component.literal("(I ate the dropped item :3)").withColor(TextColor.LIGHT_PURPLE))
		);

		callbackInfoReturnable.setReturnValue(null);
	}
}