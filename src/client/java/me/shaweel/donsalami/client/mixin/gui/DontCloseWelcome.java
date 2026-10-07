package me.shaweel.donsalami.client.mixin.gui;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.donsalami.client.gui.WelcomeScreen;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Gui.class)
public class DontCloseWelcome {
	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	private void dontCloseWelcome(@Nullable Screen screen, CallbackInfo callbackInfo) {
		if (Minecraft.getInstance().gui.screen() instanceof WelcomeScreen && !WelcomeScreen.shouldClose()) {
			callbackInfo.cancel();
		}
	}
}
