package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class OldBrokenHaybales extends SavedData {
	private static final Codec<OldBrokenHaybales> CODEC = Codec.INT.xmap(
		OldBrokenHaybales::new, 
		OldBrokenHaybales::get
	);

	private static final SavedDataType<OldBrokenHaybales> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "old_broken_haybales"),
		OldBrokenHaybales::new,
		CODEC,
		null 
	);

	private int value = 0;

	private OldBrokenHaybales() {}

	private OldBrokenHaybales(int newValue) {
		this.value = newValue;
	}

	public void set(int newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public int get() {
		return this.value;
	}

	public static OldBrokenHaybales getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new OldBrokenHaybales();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
