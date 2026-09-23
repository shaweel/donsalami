package me.shaweel.donsalami.recoursekeys;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class Dimensions {
	public static final ResourceKey<Level> THE_HUB = ResourceKey.create(
		Registries.DIMENSION,
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "the_hub")
	);

	public static final ResourceKey<Level> THE_HUB_TEMPLATE = ResourceKey.create(
		Registries.DIMENSION,
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "the_hub_template")
	);
	
	public static final ResourceKey<Level> TIDY_INVENTORY = ResourceKey.create(
		Registries.DIMENSION,
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "tidy_inventory")
	);

	public static void initialize () {}
}
