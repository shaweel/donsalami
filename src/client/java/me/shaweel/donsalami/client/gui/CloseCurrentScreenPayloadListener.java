package me.shaweel.donsalami.client.gui;

import me.shaweel.donsalami.network.CloseCurrentScreenPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;

public class CloseCurrentScreenPayloadListener {
	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(
			CloseCurrentScreenPayload.TYPE,
			CloseCurrentScreenPayload.CODEC
		);

		ClientPlayNetworking.registerGlobalReceiver(CloseCurrentScreenPayload.TYPE, (payload, context) -> {
			Minecraft.getInstance().gui.setScreen(null);
		});
	}
}
