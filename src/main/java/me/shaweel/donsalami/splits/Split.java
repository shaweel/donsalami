package me.shaweel.donsalami.splits;

import me.shaweel.donsalami.recoursekeys.Dimensions;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public enum Split {
	NONE(
		"Loading", "Loading",
		Level.OVERWORLD,
		new SpawnPosition(0, 0, 0, 0, 0),
		new Boundary(0, 0, 0, 0, 0, 0),
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	RUINED_PORTAL(
		"Ruined Portal", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(500195, 34, 500266, 0, 0),
		new Boundary(500208, 30, 500262, 500189, 50, 500278),
		IndividualSplits::ruinedPortalBeforeAdvance,
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	ELYTRA_COURSE(
		"Elytra Course", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(400087, 324, 399917, -90, 0),
		new Boundary(400050, 50, 399740, 400350, 330, 400065),
		IndividualSplits::elytraBeforeAdvance,
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	SHIPWRECK(
		"Shipwreck", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(299896, 94, 300019, -90, 0),
		new Boundary(300050, 150, 300080, 299850, 30, 299980),
		IndividualSplits::baseOverworldBeforeAdvance,
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	BURIED_TREASURE(
		"Buried Treasure", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(199797, 65, 200272, -90, 0),
		new Boundary(199750, 40, 200200, 199900, 100, 200300),
		IndividualSplits::baseOverworldBeforeAdvance,
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	DESERT_CITY(
		"Desert City", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(99952, 63, 100095, -90, 0),
		new Boundary(99900, 35, 100050, 100150, 100, 100300),
		IndividualSplits::baseOverworldBeforeAdvance,
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	IGLOO(
		"Igloo", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(-412, 66, 234, 0, 0),
		new Boundary(-500, 40, 150, -250, 100, 300),
		IndividualSplits::baseOverworldBeforeAdvance,
		(_, _) -> {},
		(_, _) -> {},
		(_, _) -> {}
	),

	PARKOUR(
		"Parkour", "Enter the Nether",
		Dimensions.TIDY_INVENTORY,
		new SpawnPosition(0, 100, 0, 180, 0),
		new Boundary(-20, 125, -20, 20, 90, 20),
		IndividualSplits::parkourBeforeAdvance,
		IndividualSplits::parkourAfterAdvance,
		(_, _) -> {},
		(_, _) -> {}
	),

	THE_HUB(
		"The Hub", "Enter The Nether",
		Level.OVERWORLD,
		new SpawnPosition(119, 129, -576, 180, 0),
		new Boundary(-170, 50, -285, 325, 270, -755),
		IndividualSplits::hubBeforeAdvance,
		IndividualSplits::hubAfterAdvance,
		(_, _) -> {},
		(_, _) -> {}
	),

	THE_NETHER(
		"The Nether", "Find the Bastion",
		Level.NETHER,
		new SpawnPosition(0, 100, 0, 180, 0),
		new Boundary(-300, -10, -300, 300, 128, 300),
		(_, _) -> {},
		IndividualSplits::netherAfterAdvance,
		(_, _) -> {},
		(_, _) -> {}
	);

	public record SpawnPosition(int x, int y, int z, float yRot, float xRot) {}
	public record Boundary(int x0, int y0, int z0, int x1, int y1, int z1) {}

	public final String displayName;
	public final String objective;
	public final ResourceKey<Level> dimension;
	public final SpawnPosition spawnPosition;
	public final Boundary boundary;
	public final BiConsumer<MinecraftServer, ServerPlayer> beforeAdvance;
	public final BiConsumer<MinecraftServer, ServerPlayer> afterAdvance;
	public final BiConsumer<MinecraftServer, ServerPlayer> beforeReset;
	public final BiConsumer<MinecraftServer, ServerPlayer> afterReset;

	public @Nullable Split next() {
		int i = ordinal() + 1;
		return i < values().length ? values()[i] : null;
	}

	private Split(String displayName, String objective, ResourceKey<Level> dimension, SpawnPosition spawnPosition, Boundary boundary) {
		this(displayName, objective, dimension, spawnPosition, boundary, (_, _) -> {}, (_, _) -> {}, (_, _) -> {}, (_, _) -> {});
	}

	private Split(
		String displayName, String objective, ResourceKey<Level> dimension, SpawnPosition spawnPosition, Boundary boundary,
		BiConsumer<MinecraftServer, ServerPlayer> beforeAdvance,
		BiConsumer<MinecraftServer, ServerPlayer> afterAdvance,
		BiConsumer<MinecraftServer, ServerPlayer> beforeReset,
		BiConsumer<MinecraftServer, ServerPlayer> afterReset
	) {
		this.displayName = displayName;
		this.objective = objective;
		this.dimension = dimension;
		this.spawnPosition = spawnPosition;
		this.boundary = boundary;
		this.beforeAdvance = beforeAdvance;
		this.afterAdvance = afterAdvance;
		this.beforeReset = beforeReset;
		this.afterReset = afterReset;
	}
}
