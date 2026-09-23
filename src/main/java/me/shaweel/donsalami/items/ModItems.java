package me.shaweel.donsalami.items;

import java.util.List;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;

public class ModItems {
	public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
		Item item = itemFactory.apply(settings.setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return item;
	}

	public static final Item SALAMI = register(ModItemIds.SALAMI, Salami::new, new Item.Properties().food(
		new FoodProperties.Builder()
			.nutrition(20)
			.saturationModifier(1)
			.build(),
		new Consumable(10, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true, List.of())
	));

	public static final Item MAX_SPEED = register(ModItemIds.MAX_SPEED, Item::new, new Item.Properties());
	public static final Item BLACK_CAT = register(ModItemIds.BLACK_CAT, Item::new, new Item.Properties());
	public static final Item TAS = register(ModItemIds.TAS, Item::new, new Item.Properties());

	public static void initialize() {}
}
