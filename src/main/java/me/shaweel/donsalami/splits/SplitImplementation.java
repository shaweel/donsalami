package me.shaweel.donsalami.splits;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import me.shaweel.donsalami.network.CloseCurrentScreenPayload;
import me.shaweel.donsalami.network.ResetSplitPayload;
import me.shaweel.donsalami.network.ShowGenericMessageScreenPayload;
import me.shaweel.donsalami.utils.Utils;
import me.shaweel.donsalami.worldData.CurrentSplit;
import me.shaweel.donsalami.worldData.SavedInventories;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelData.RespawnData;

public class SplitImplementation {
	private static List<MinecraftServer> initializedServers = new ArrayList<>();

	public static String getCurrentSplit(MinecraftServer server) {
		return CurrentSplit.getData(server).get();
	}

	public static @Nullable String getNextSplit(MinecraftServer server) {
		Split nextSplit = Split.valueOf(getCurrentSplit(server)).next();
		return nextSplit == null ? null : nextSplit.name();
	}

	private static void saveInventory(MinecraftServer server, ServerPlayer player) {
		String split = getCurrentSplit(server);
		Map<String, Map<Integer, ItemStack>> savedInventories = SavedInventories.getData(server).get();
		Map<String, Map<Integer, ItemStack>> mutableSavedInventories = new HashMap<>(savedInventories);

		Map<Integer, ItemStack> inventoryToSave = new HashMap<>();
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (player.getInventory().getItem(i).isEmpty()) continue;
			inventoryToSave.put(i, player.getInventory().getItem(i).copy());
		}

		mutableSavedInventories.put(split, inventoryToSave);

		SavedInventories.getData(server).set(mutableSavedInventories);
	}

	private static void loadInventory(MinecraftServer server, ServerPlayer player) {
		player.closeContainer();
		player.getInventory().clearContent();
		
		String split = getCurrentSplit(server);
		Map<String, Map<Integer, ItemStack>> savedInventories = SavedInventories.getData(server).get();

		Map<Integer, ItemStack> inventoryToLoad = savedInventories.get(split);
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (!inventoryToLoad.containsKey(i)) continue;
			player.getInventory().setItem(i, inventoryToLoad.get(i).copy());
		}
	}

	public static void advanceSplit(MinecraftServer server) {
		ServerPlayer player = Utils.getPlayer(server);
		if (player == null) return;

		String nextSplitName = getNextSplit(server);
		Split nextSplit = Split.valueOf(nextSplitName);

		nextSplit.beforeAdvance.accept(server, player);

		CurrentSplit.getData(server).set(nextSplitName);

		RespawnConfig newRespawnConfig = new RespawnConfig(
			new RespawnData(
				new GlobalPos(
					nextSplit.dimension,
					new BlockPos(
						nextSplit.spawnPosition.x(),
						nextSplit.spawnPosition.y(),
						nextSplit.spawnPosition.z()
					)
				),
				nextSplit.spawnPosition.yRot(),
				nextSplit.spawnPosition.xRot()
			), 
			true
		);

		player.setRespawnPosition(newRespawnConfig, false);

		saveInventory(server, player);

		resetSplit(server, "Loading");

		nextSplit.afterAdvance.accept(server, player);
	}

	public static void resetSplit(MinecraftServer server) {
		resetSplit(server, "Resetting");
	}

	public static void resetSplit(MinecraftServer server, String screenMessage) {
		ServerPlayer player = Utils.getPlayer(server);
		if (player == null) return;

		ServerPlayNetworking.send(player, new ShowGenericMessageScreenPayload(Component.literal(screenMessage)));

		Split split = Split.valueOf(getCurrentSplit(server));
		split.beforeReset.accept(server, player);

		try {
			resetFromSplitSnapshot(server);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		player.setHealth(0);
		loadInventory(server, player);

		split.afterReset.accept(server, player);
	}

	private static void createSplitSnapshot(ServerLevel level, Split.Boundary boundary, String splitName) throws IOException {
		int minX = Math.min(boundary.x0(), boundary.x1()) - 7;
		int minY = Math.min(boundary.y0(), boundary.y1()) - 7;
		int minZ = Math.min(boundary.z0(), boundary.z1()) - 7;
		int maxX = Math.max(boundary.x0(), boundary.x1()) + 7;
		int maxY = Math.max(boundary.y0(), boundary.y1()) + 7;
		int maxZ = Math.max(boundary.z0(), boundary.z1()) + 7;

		CompoundTag root = new CompoundTag();

		ListTag blockPallete = new ListTag();
		Map<BlockState, Integer> blockPalleteIds = new HashMap<>();
		List<Long> packedBlocks = new ArrayList<>();

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockState blockState = level.getBlockState(new BlockPos(x, y, z));

					if (blockState.isAir()) {
						continue;
					}

					Integer blockStateId = blockPalleteIds.get(blockState);

					if (blockStateId == null) {
						blockStateId = blockPallete.size();

						if (blockStateId >= 8192) {
							throw new IllegalStateException("Too many unique blocks: " + blockStateId);
						}

						blockPalleteIds.put(blockState, blockStateId);

						CompoundTag blockStateTag = new CompoundTag();

						blockStateTag.store("blockState", BlockState.CODEC, blockState);
						blockPallete.add(blockStateTag);
					}

					int relativeX = x - minX;
					int relativeY = y - minY;
					int relativeZ = z - minZ;

					if (relativeX >= 131072 || relativeY >= 131072 || relativeZ >= 131072) {
						throw new IllegalArgumentException("Snapshot is too large (larger than a 17 bit integer in one or more axes)");
					}

					long packedBlock = ((long) relativeX) | ((long) relativeY << 17) | ((long) relativeZ << 34) | ((long) blockStateId << 51);
					packedBlocks.add(packedBlock);
				}
			}
		}

		root.put("blockPallete", blockPallete);

		long[] packedBlocksArray = new long[packedBlocks.size()];

		for (int i = 0; i < packedBlocks.size(); i++) {
			packedBlocksArray[i] = packedBlocks.get(i);
		}

		root.put("packedBlocks", new LongArrayTag(packedBlocksArray));

		Path worldDirectory = level.getServer().getWorldPath(LevelResource.ROOT);
		Path destination = worldDirectory.resolve(String.format("split_snapshots/%s.nbt", splitName));
		Files.createDirectories(destination.getParent());
		NbtIo.writeCompressed(root, destination);
	}

	public static void createSplitSnapshots(MinecraftServer server, ServerPlayer player) {
		for (Split split : Split.values()) {
			try {
				ServerPlayNetworking.send(player, new ShowGenericMessageScreenPayload(
					Component.literal("Creating split snapshot ")
					.append(Component.literal(split.displayName).withStyle(ChatFormatting.ITALIC))
					.append(Component.literal(String.format(" - %s/%s", split.ordinal(), Split.values().length - 1)).withStyle(ChatFormatting.RESET))
				));
				createSplitSnapshot(server.getLevel(split.dimension), split.boundary, split.name());
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		ServerPlayNetworking.send(player, new CloseCurrentScreenPayload());
	}	

	public static void resetFromSplitSnapshot(MinecraftServer server) throws IOException {
		Split split = Split.valueOf(getCurrentSplit(server));
		
		ServerLevel level = server.getLevel(split.dimension);

		int minX = Math.min(split.boundary.x0(), split.boundary.x1()) - 7;
		int minY = Math.min(split.boundary.y0(), split.boundary.y1()) - 7;
		int minZ = Math.min(split.boundary.z0(), split.boundary.z1()) - 7;
		int maxX = Math.max(split.boundary.x0(), split.boundary.x1()) + 7;
		int maxY = Math.max(split.boundary.y0(), split.boundary.y1()) + 7;
		int maxZ = Math.max(split.boundary.z0(), split.boundary.z1()) + 7;

		CompoundTag splitSnapshot = NbtIo.readCompressed(
			server.getWorldPath(LevelResource.ROOT).resolve(String.format("split_snapshots/%s.nbt", split.name())),
			NbtAccounter.unlimitedHeap()
		);
		
		ListTag blockPallete = splitSnapshot.getList("blockPallete").orElseThrow();
		long[] packedBlocks = splitSnapshot.getLongArray("packedBlocks").orElseThrow();

		Set<BlockPos> usedBlockPositions = new HashSet<>();

		for (long packedBlock : packedBlocks) {
			int relativeX = (int) (packedBlock & 0x1FFFFL);
			int relativeY = (int) ((packedBlock >> 17) & 0x1FFFFL);
			int relativeZ = (int) ((packedBlock >> 34) & 0x1FFFFL);
			int blockStateId = (int) ((packedBlock >> 51) & 0x1FFFL);

			BlockPos blockPos = new BlockPos(relativeX + minX, relativeY + minY, relativeZ + minZ);

			usedBlockPositions.add(blockPos);

			CompoundTag compoundStateTag = blockPallete.getCompound(blockStateId).orElseThrow();
			BlockState blockState = compoundStateTag.read("blockState", BlockState.CODEC).orElse(null);
			BlockState currentBlockState = level.getBlockState(blockPos);

			if (currentBlockState == blockState) continue;

			level.setBlockAndUpdate(blockPos, blockState);
		}

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					boolean isCurrentlyAir = level.getBlockState(blockPos).isAir();

					if (usedBlockPositions.contains(blockPos) || isCurrentlyAir) continue;

					level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
				}
			}
		}
	}

	private static void initializeServer(MinecraftServer server) {
		if (initializedServers.contains(server)) return;

		Split split = Split.valueOf(getCurrentSplit(server));
		if (split == Split.NONE) {
			advanceSplit(server);
		}

		initializedServers.add(server);
	}

	private static void respectBoundary(MinecraftServer server) {
		ServerPlayer player = Utils.getPlayer(server);
		Split split = Split.valueOf(getCurrentSplit(server));

		double playerX = player.getX();
		double playerY = player.getY();
		double playerZ = player.getZ();

		int minX = Math.min(split.boundary.x0(), split.boundary.x1());
		int minY = Math.min(split.boundary.y0(), split.boundary.y1());
		int minZ = Math.min(split.boundary.z0(), split.boundary.z1());
		int maxX = Math.max(split.boundary.x0(), split.boundary.x1());
		int maxY = Math.max(split.boundary.y0(), split.boundary.y1());
		int maxZ = Math.max(split.boundary.z0(), split.boundary.z1());

		if (
			playerX <= maxX && playerX >= minX &&
			playerY <= maxY && playerY >= minY &&
			playerZ <= maxZ && playerZ >= minZ
		) return;

		/*player.connection.send(new ClientboundSetActionBarTextPacket(
			Component.literal("You cannot go too far away from the map.").withColor(TextColor.RED)
		));

		player.teleport(new TeleportTransition(
			server.getLevel(split.dimension),
			new Vec3(split.spawnPosition.x(), split.spawnPosition.y(), split.spawnPosition.z()),
			Vec3.ZERO,
			split.spawnPosition.yRot(),
			split.spawnPosition.xRot(),
			TeleportTransition.DO_NOTHING
		));*/
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			initializeServer(server);
			respectBoundary(server);
		});

		PayloadTypeRegistry.serverboundPlay().register(
			ResetSplitPayload.TYPE,
			ResetSplitPayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(ResetSplitPayload.TYPE, (payload, context) -> {
			MinecraftServer server = context.player().level().getServer();

			resetSplit(server);
		});
	}
}
