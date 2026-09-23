package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SoldWheat extends SavedData {
	private static final Codec<SoldWheat> CODEC = Codec.BOOL.xmap(
		SoldWheat::new, 
		SoldWheat::get
	);

	private static final SavedDataType<SoldWheat> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "sold_wheat"),
		SoldWheat::new,
		CODEC,
		null 
	);

	private boolean value = false;

	private SoldWheat() {}

	private SoldWheat(boolean newValue) {
		this.value = newValue;
	}

	public void set(boolean newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public boolean get() {
		return this.value;
	}

	public static SoldWheat getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new SoldWheat();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
