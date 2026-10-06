package me.shaweel.donsalami.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

@Mixin(Hud.class)
public class RemoveHungerAndXpBars {
	private static final Identifier ARMOR_EMPTY_SPRITE = Identifier.withDefaultNamespace("hud/armor_empty");
	private static final Identifier ARMOR_HALF_SPRITE = Identifier.withDefaultNamespace("hud/armor_half");
	private static final Identifier ARMOR_FULL_SPRITE = Identifier.withDefaultNamespace("hud/armor_full");

	@Shadow private SpectatorGui spectatorGui;
	@Shadow private void extractItemHotbar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {}
	@Shadow private void extractPlayerHealth(GuiGraphicsExtractor graphics) {}
	@Shadow private void extractVehicleHealth(GuiGraphicsExtractor graphics) {}

	@Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
	private void cancelExtractFood(GuiGraphicsExtractor graphics, Player player, int yLineBase, int xRight, CallbackInfo callbackInfo) {
		callbackInfo.cancel();
	}

	@Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
	private void removeXpBar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
		if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR) {
			spectatorGui.extractHotbar(graphics);
		} else {
			extractItemHotbar(graphics, deltaTracker);
		}

		if (Minecraft.getInstance().gameMode.canHurtPlayer()) {
			extractPlayerHealth(graphics);
		}

		extractVehicleHealth(graphics);

		callbackInfo.cancel();
	}

	@ModifyExpressionValue(
		method = "extractPlayerHealth",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;guiHeight()I"
		)
	)
	private int moveHeartsDown(int original) {
		return original + 6;
	}

	@Inject(method = "extractArmor", at = @At("HEAD"), cancellable = true)
	private static void moveArmorBar(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, CallbackInfo callbackInfo) {
		int xRight = graphics.guiWidth() / 2 + 91;
		
		int armor = player.getArmorValue();
		if (armor <= 0) {
			callbackInfo.cancel();
			return;
		}

		for (int i = 0; i < 10; ++i) {
			int xo = xRight - i * 8 - 9;

			if (i * 2 + 1 < armor) {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_FULL_SPRITE, xo, yLineBase, 9, 9);
			}

			if (i * 2 + 1 == armor) {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_HALF_SPRITE, xo, yLineBase, 9, 9);
			}

			if (i * 2 + 1 > armor) {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_SPRITE, xo, yLineBase, 9, 9);
			}
		}

		callbackInfo.cancel();
	}
}
