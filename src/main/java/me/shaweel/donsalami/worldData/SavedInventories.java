package me.shaweel.donsalami.worldData;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SavedInventories extends SavedData {
	private static final Codec<SavedInventories> CODEC = Codec.unboundedMap(
		Codec.STRING,
		Codec.unboundedMap(
			Codec.STRING.xmap(
				Integer::parseInt,
				String::valueOf
			), 
			ItemStack.CODEC
		)
	).xmap(SavedInventories::new, SavedInventories::get);

	private static final SavedDataType<SavedInventories> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "the_hub_enter_inventory"),
		SavedInventories::new,
		CODEC,
		null 
	);

	private Map<String, Map<Integer, ItemStack>> value = new HashMap<>();

	private SavedInventories() {}

	private SavedInventories(Map<String, Map<Integer, ItemStack>> newValue) {
		this.value = newValue;
	}

	public void set(Map<String, Map<Integer, ItemStack>> newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public Map<String, Map<Integer, ItemStack>> get() {
		return this.value;
	}

	public static SavedInventories getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new SavedInventories();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
