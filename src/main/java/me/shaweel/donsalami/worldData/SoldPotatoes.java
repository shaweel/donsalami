package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SoldPotatoes extends SavedData {
	private static final Codec<SoldPotatoes> CODEC = Codec.BOOL.xmap(
		SoldPotatoes::new, 
		SoldPotatoes::get
	);

	private static final SavedDataType<SoldPotatoes> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "sold_potatoes"),
		SoldPotatoes::new,
		CODEC,
		null 
	);

	private boolean value = false;

	private SoldPotatoes() {}

	private SoldPotatoes(boolean newValue) {
		this.value = newValue;
	}

	public void set(boolean newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public boolean get() {
		return this.value;
	}

	public static SoldPotatoes getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new SoldPotatoes();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
