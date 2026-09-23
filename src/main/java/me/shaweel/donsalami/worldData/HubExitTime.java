package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class HubExitTime extends SavedData {
	private static final Codec<HubExitTime> CODEC = Codec.INT.xmap(
		HubExitTime::new, 
		HubExitTime::get
	);

	private static final SavedDataType<HubExitTime> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "nether_portal_skyblock_time"),
		HubExitTime::new,
		CODEC,
		null 
	);

	private int value = 0;

	private HubExitTime() {}

	private HubExitTime(int newValue) {
		this.value = newValue;
	}

	public void set(int newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public int get() {
		return this.value;
	}

	public static HubExitTime getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new HubExitTime();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
