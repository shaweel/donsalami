package me.shaweel.donsalami.worldData;

import com.mojang.serialization.Codec;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class InHubChallenge extends SavedData {
	private static final Codec<InHubChallenge> CODEC = Codec.BOOL.xmap(
		InHubChallenge::new, 
		InHubChallenge::get
	);

	private static final SavedDataType<InHubChallenge> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "in_hub_challenge"),
		InHubChallenge::new,
		CODEC,
		null 
	);

	private boolean value = false;

	private InHubChallenge() {}

	private InHubChallenge(boolean newValue) {
		this.value = newValue;
	}

	public void set(boolean newValue) {
		this.value = newValue;
		this.setDirty();
	}

	public boolean get() {
		return this.value;
	}

	public static InHubChallenge getData(MinecraftServer server) {
		ServerLevel level = server.getLevel(ServerLevel.NETHER);

		if (level == null) {
			return new InHubChallenge();
		}
		
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
}
