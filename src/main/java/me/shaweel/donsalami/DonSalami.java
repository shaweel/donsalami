package me.shaweel.donsalami;

import me.shaweel.donsalami.items.ModItems;
import me.shaweel.donsalami.miscellaneous.DontBreakBastion;
import me.shaweel.donsalami.miscellaneous.Elytra;
import me.shaweel.donsalami.miscellaneous.TheHubChallenge;
import me.shaweel.donsalami.miscellaneous.KillZone;
import me.shaweel.donsalami.recoursekeys.Dimensions;
import me.shaweel.donsalami.recoursekeys.Structures;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class DonSalami implements ModInitializer {
	public static final String MOD_ID = "donsalami";

	@Override
	public void onInitialize() {
		Dimensions.initialize();
		Structures.initialize();
		DontBreakBastion.initialize();
		TheHubChallenge.initialize();
		ModItems.initialize();
		KillZone.initialize();
		Elytra.initialize();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
