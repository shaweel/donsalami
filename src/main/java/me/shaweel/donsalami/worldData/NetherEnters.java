package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class NetherEnters extends SavedData {
	private static final Codec<NetherEnters> CODEC = Codec.INT.xmap(
		NetherEnters::new, 
		NetherEnters::get
	);

	private static final SavedDataType<NetherEnters> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "nether_enter"),
		NetherEnters::new,
		CODEC,
		null 
	);

	private int value = 0;

	private NetherEnters() {}

	private NetherEnters(int newValue) {
		this.value = newValue;
	}

	public void set(int newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public int get() {
		return this.value;
	}

	public static NetherEnters getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new NetherEnters();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
