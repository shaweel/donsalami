package me.shaweel.donsalami.splits;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

public enum Split {
	/*private static final int[] x = new int[]{400087, 299896, 199797, 99952, -412};
	private static final int[] y = new int[]{324, 94, 65, 63, 66};
	private static final int[] z = new int[]{399917, 300019, 200272, 100095, 234};*/
	RUINED_PORTAL(
		"Ruined Portal",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			500195, 34, 500266
		)),
		180,
		0
	),
	ELYTRA_COURSE(
		"Elytra Course",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			400087, 324, 399917
		)),
		180,
		0
	),
	SHIPWRECK(
		"Shipwreck",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			299896, 94, 300019
		)),
		180,
		0
	),
	BURIED_TREASURE(
		"Buried Treasure",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			199797, 65, 200272
		)),
		180,
		0
	),
	DESERT_CITY(
		"Desert City",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			99952, 63, 100095
		)),
		180,
		0
	),
	IGLOO(
		"Igloo",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			-412, 66, 234
		)),
		0,
		0
	),
	PARKOUR(
		"Parkour",
		"Enter the Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			400087, 324, 399917
		)),
		180,
		0
	),
	THE_HUB(
		"The Hub",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			400087, 324, 399917
		)),
		180,
		0
	),
	THE_NETHER(
		"The Nether",
		"Enter The Nether",
		new GlobalPos(Level.OVERWORLD, new BlockPos(
			400087, 324, 399917
		)),
		180,
		0
	);

	public final String displayName;
	public final String objective;
	public Runnable onReset = () -> {};

	public @Nullable Split next() {
		int i = ordinal() + 1;
		return i < values().length ? values()[i] : null;
	}

	private Split(String displayName, String objective, GlobalPos position, double yRot, double xRot) {
		this.displayName = displayName;
		this.objective = objective;
	}

	private Split(String displayName, String objective, GlobalPos position, double yRot, double xRot, Runnable onReset) {
		this.displayName = displayName;
		this.objective = displayName;
		this.onReset = onReset;
	}
}
