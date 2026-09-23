package me.shaweel.donsalami.recoursekeys;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class Structures {
	public static final ResourceKey<Structure> DUNGEON_BASTION = ResourceKey.create(
		Registries.STRUCTURE,
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "dungeon_bastion")
	);

	public static void initialize () {}
}
