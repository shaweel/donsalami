package me.shaweel.donsalami.splits;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

public enum Split {
	RUINED_PORTAL("Ruined Portal", "Enter The Nether"),
	ELYTRA_COURSE("Elytra Course", "Enter The Nether"),
	SHIPWRECK("Shipwreck", "Enter The Nether"),
	BURIED_TREASURE("Buried Treasure", "Enter The Nether"),
	DESERT_CITY("Desert City", "Enter The Nether"),
	IGLOO("Igloo", "Enter The Nether"),
	THE_HUB("The Hub", "Enter The Nether"),
	THE_NETHER("The Nether", "Find The Bastion");

	public final String displayName;
	public final String objective;
	public Runnable onStart = () -> {};
	public Runnable onReset = () -> {};

	public @Nullable Split next() {
		int i = ordinal() + 1;
		return i < values().length ? values()[i] : null;
	}

	private Split(String displayName, String objective, GlobalPos position) {
		this.displayName = displayName;
		this.objective = objective;
	}

	private Split(String displayName, String objective, GlobalPos position, Runnable onStart, Runnable onReset) {
		this.displayName = displayName;
		this.objective = displayName;
		this.onStart = onStart;
		this.onReset = onReset;
	}
}
