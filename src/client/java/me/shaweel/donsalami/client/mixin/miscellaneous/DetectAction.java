package me.shaweel.donsalami.client.mixin.miscellaneous;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import me.shaweel.donsalami.network.PressPayload;
import me.shaweel.donsalami.the_game.the_hub.TheHubChallenge;

import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public class DetectAction {
	@Inject(at = @At("HEAD"), method = "click")
	private static void detectAction(InputConstants.Key key, CallbackInfo callbackInfo) {
		if (TheHubChallenge.resetting) return;

		ClientPlayNetworking.send(new PressPayload());
	}
}
