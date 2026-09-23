package me.shaweel.donsalami.client;

import com.mojang.blaze3d.platform.InputConstants;

import me.shaweel.donsalami.DonSalami;
import me.shaweel.donsalami.network.ResetHubPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class KeyMappings {
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "donsalami")
	);

	public static final KeyMapping resetHubChallenge = KeyMappingHelper.registerKeyMapping(new KeyMapping(
		"key.donsalami.reset_hub_challenge",
		InputConstants.Type.KEYSYM,
		InputConstants.KEY_RETURN,
		CATEGORY
	));

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (resetHubChallenge.consumeClick()) {
				ClientPlayNetworking.send(new ResetHubPayload());
			}
		});
	}
}
