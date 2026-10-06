package me.shaweel.donsalami.client;

import me.shaweel.donsalami.DonSalami;
import me.shaweel.donsalami.splits.Split;
import me.shaweel.donsalami.splits.SplitImplementation;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;

public class SplitClient {
	private static final int X_PADDING = 10;
	private static final int Y_PADDING = 10;
	private static final int BACKGROUND_PADDING = 2;
	
	public static void initialize() {
		HudElementRegistry.addLast(DonSalami.id("split_hud"), SplitClient::extractRenderState);
	}

	public static void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
			return;
		}

		Font font = Minecraft.getInstance().font;
		MinecraftServer currentServer = Minecraft.getInstance().getSingleplayerServer();

		String currentSplit;
		if (currentServer != null) {
			currentSplit = SplitImplementation.getCurrentSplit(currentServer);
		} else {
			currentSplit = "RUINED_PORTAL";
		}


		Component[] lines = new Component[] {
			Component.literal("Splits").withStyle(ChatFormatting.BOLD).withColor(TextColor.YELLOW),

			Component.literal("Splits: ").withColor(TextColor.GRAY)
			.append(Component.literal(String.valueOf(Split.valueOf(currentSplit).ordinal() + 1)).withColor(TextColor.WHITE))
			.append(Component.literal("/" + String.valueOf(Split.values().length)).withColor(TextColor.WHITE)),

			Component.literal("Current Split: ").withColor(TextColor.GRAY)
			.append(Component.literal(Split.valueOf(currentSplit).displayName).withColor(TextColor.WHITE)),

			Component.literal("Objective: ").withColor(TextColor.GRAY)
			.append(Component.literal(Split.valueOf(currentSplit).objective).withColor(TextColor.WHITE))
		};

		int width = 0;
		int backgroundColor = Minecraft.getInstance().options.getBackgroundColor(0.0F);

		for (int index = 0; index < lines.length; index++) {
			int lineWidth = font.width(lines[index]);

			if (lineWidth > width) {
				width = lineWidth;
			}
		}

		graphics.fill(
			X_PADDING - BACKGROUND_PADDING - 1,
			Y_PADDING - BACKGROUND_PADDING - 1,
			X_PADDING + width + BACKGROUND_PADDING,
			Y_PADDING + lines.length * (font.lineHeight + 1) + BACKGROUND_PADDING - 1,
			backgroundColor
		);

		for (int index = 0; index < lines.length; index++) {
			graphics.text(font, lines[index], X_PADDING, Y_PADDING + (font.lineHeight + 1) * index, 0xFFFFFFFF);
		}
	}
}
