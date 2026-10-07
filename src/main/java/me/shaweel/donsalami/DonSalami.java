package me.shaweel.donsalami;

import me.shaweel.donsalami.healing.FoodHealing;
import me.shaweel.donsalami.items.ModItems;
import me.shaweel.donsalami.recoursekeys.Dimensions;
import me.shaweel.donsalami.recoursekeys.Structures;
import me.shaweel.donsalami.the_game.Elytra;
import me.shaweel.donsalami.the_game.ParkourKillZone;
import me.shaweel.donsalami.the_game.bastion.DontBreakBastion;
import me.shaweel.donsalami.the_game.the_hub.TheHubChallenge;
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
		ParkourKillZone.initialize();
		Elytra.initialize();
		FoodHealing.initialize();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
