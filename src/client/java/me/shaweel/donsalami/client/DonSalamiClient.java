package me.shaweel.donsalami.client;

import net.fabricmc.api.ClientModInitializer;

public class DonSalamiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyMappings.initialize();
		CutsceneRenderer.initialize();
		SplitClient.initialize();
	}
}