package me.shaweel.donsalami.client.gui;

import me.shaweel.donsalami.network.ShowGenericMessageScreenPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;

public class GenericMessageScreenPayloadListener {
	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(
			ShowGenericMessageScreenPayload.TYPE,
			ShowGenericMessageScreenPayload.CODEC
		);

		ClientPlayNetworking.registerGlobalReceiver(ShowGenericMessageScreenPayload.TYPE, (payload, context) -> {
			Minecraft.getInstance().setScreenAndShow(new GenericMessageScreen(payload.message()));
		});
	}
}
