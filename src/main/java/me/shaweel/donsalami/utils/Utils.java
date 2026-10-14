package me.shaweel.donsalami.utils;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Utils {
	private static int frozenFor = 0;

	private static boolean initialized = false;

	public static @Nullable ServerPlayer getPlayer(MinecraftServer server) {
		if (!server.isSingleplayer()) {
			throw new IllegalStateException("This mod is singleplayer-only.");
		}
		if (server.getPlayerList().getPlayers().isEmpty()) return null;

		return server.getPlayerList().getPlayers().get(0);
	}

	public static void freezeFor(int ticks, MinecraftServer server) {
		if (!initialized) initialize();

		server.tickRateManager().setFrozen(true);
		frozenFor = ticks;
	}

	private static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (frozenFor <= 0) { return; }
			frozenFor--;
			if (frozenFor <= 0) { server.tickRateManager().setFrozen(false); }
		});
	}
}
