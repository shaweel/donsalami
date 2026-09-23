package me.shaweel.donsalami.items;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
	public static ResourceKey<Item> create(String name) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, name));
	}

	public static final ResourceKey<Item> SALAMI = create("salami");
	public static final ResourceKey<Item> MAX_SPEED = create("max_speed");
	public static final ResourceKey<Item> BLACK_CAT = create("black_cat");
	public static final ResourceKey<Item> TAS = create("tas");
}
