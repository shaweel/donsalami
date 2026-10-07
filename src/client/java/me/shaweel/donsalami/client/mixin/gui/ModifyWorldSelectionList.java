package me.shaweel.donsalami.client.mixin.gui;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;

@Mixin(WorldSelectionList.class)
public class ModifyWorldSelectionList {
	@Inject(
		method = "handleNewLevels",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;openFresh(Lnet/minecraft/client/Minecraft;Ljava/lang/Runnable;)V"
		),
		cancellable = true
	)
	private void dontMakeNewWorld(List<LevelSummary> levels, CallbackInfo callbackInfo) {
		Minecraft.getInstance().gui.setScreen(new TitleScreen());
		callbackInfo.cancel();
	}
}
