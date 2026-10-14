package me.shaweel.donsalami.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class Log {
	public static void debug(Component component) {
		if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
			return;
		}

		MinecraftServer server = CurrentServer.get();

		if (server == null) {
			System.out.println(component.getString());
		}

		server.sendSystemMessage(Component.literal("[DEBUG] ").withStyle(ChatFormatting.YELLOW).append(component));
	}
}
