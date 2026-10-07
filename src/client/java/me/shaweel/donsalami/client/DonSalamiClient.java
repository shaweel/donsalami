package me.shaweel.donsalami.client;

import me.shaweel.donsalami.client.gui.SplitsHud;
import me.shaweel.donsalami.client.render.CutsceneRenderer;
import net.fabricmc.api.ClientModInitializer;

public class DonSalamiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyMappings.initialize();
		CutsceneRenderer.initialize();
		SplitsHud.initialize();
	}
}