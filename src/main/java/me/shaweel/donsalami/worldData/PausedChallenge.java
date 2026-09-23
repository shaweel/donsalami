package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class PausedChallenge extends SavedData {
	private static final Codec<PausedChallenge> CODEC = Codec.BOOL.xmap(
		PausedChallenge::new, 
		PausedChallenge::get
	);

	private static final SavedDataType<PausedChallenge> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "paused_challenge"),
		PausedChallenge::new,
		CODEC,
		null 
	);

	private boolean value = false;

	private PausedChallenge() {}

	private PausedChallenge(boolean newValue) {
		this.value = newValue;
	}

	public void set(boolean newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public boolean get() {
		return this.value;
	}

	public static PausedChallenge getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new PausedChallenge();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
