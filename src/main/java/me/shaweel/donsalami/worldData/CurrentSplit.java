package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import me.shaweel.donsalami.splits.Split;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class CurrentSplit extends SavedData {
	private static final Codec<CurrentSplit> CODEC = Codec.STRING.xmap(
		CurrentSplit::new, 
		CurrentSplit::get
	);

	private static final SavedDataType<CurrentSplit> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "in_hub_challenge"),
		CurrentSplit::new,
		CODEC,
		null 
	);

	private String value = Split.values()[0].name();

	private CurrentSplit() {}

	private CurrentSplit(String newValue) {
		this.value = newValue;
	}

	public void set(String newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public String get() {
		return this.value;
	}

	public static CurrentSplit getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

		if (level == null) {
			return new CurrentSplit();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
