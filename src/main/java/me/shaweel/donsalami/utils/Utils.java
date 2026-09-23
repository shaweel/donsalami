package me.shaweel.donsalami.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class Utils {
	private static int frozenFor = 0;

	private static boolean initialized = false;

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
