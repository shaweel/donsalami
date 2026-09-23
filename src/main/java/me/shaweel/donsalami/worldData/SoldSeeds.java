package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SoldSeeds extends SavedData {
	private static final Codec<SoldSeeds> CODEC = Codec.BOOL.xmap(
		SoldSeeds::new, 
		SoldSeeds::get
	);

	private static final SavedDataType<SoldSeeds> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "sold_seeds"),
		SoldSeeds::new,
		CODEC,
		null 
	);

	private boolean value = false;

	private SoldSeeds() {}

	private SoldSeeds(boolean newValue) {
		this.value = newValue;
	}

	public void set(boolean newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public boolean get() {
		return this.value;
	}

	public static SoldSeeds getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new SoldSeeds();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
