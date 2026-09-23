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

public class TheHubEnterInventory extends SavedData {
	private static final Codec<TheHubEnterInventory> CODEC = Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, String::valueOf), ItemStack.CODEC).xmap(
		TheHubEnterInventory::new, 
		TheHubEnterInventory::get
	);

	private static final SavedDataType<TheHubEnterInventory> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "the_hub_enter_inventory"),
		TheHubEnterInventory::new,
		CODEC,
		null 
	);

	private Map<Integer, ItemStack> value = new HashMap<>();

	private TheHubEnterInventory() {}

	private TheHubEnterInventory(Map<Integer, ItemStack> newValue) {
		this.value = newValue;
	}

	public void set(Map<Integer, ItemStack> newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public Map<Integer, ItemStack> get() {
		return this.value;
	}

	public static TheHubEnterInventory getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new TheHubEnterInventory();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
