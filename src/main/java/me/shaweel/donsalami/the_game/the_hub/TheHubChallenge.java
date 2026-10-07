package me.shaweel.donsalami.the_game.the_hub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.shaweel.donsalami.network.PressPayload;
import me.shaweel.donsalami.network.ResetSplitPayload;
import me.shaweel.donsalami.recoursekeys.Dimensions;
import me.shaweel.donsalami.worldData.InHubChallenge;
import me.shaweel.donsalami.worldData.HubExitTime;
import me.shaweel.donsalami.worldData.OldBrokenHaybales;
import me.shaweel.donsalami.worldData.PausedChallenge;
import me.shaweel.donsalami.worldData.SoldPotatoes;
import me.shaweel.donsalami.worldData.SoldSeeds;
import me.shaweel.donsalami.worldData.SoldWheat;
import me.shaweel.donsalami.worldData.TheHubEnterInventory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData.RespawnData;
import net.minecraft.world.phys.Vec3;

public class TheHubChallenge {
	public static int timeLimit = 65 * 20;
	public static boolean resetting = false;

	private static void notifyFinishedReset(ServerPlayer player) {
		player.connection.send(new ClientboundSetTitlesAnimationPacket(0, 30, 10));

		player.connection.send(new ClientboundSetTitleTextPacket(
			Component.empty()
		));
		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Component.literal("Successfully reset").withColor(TextColor.RED)
		));
	}

	private static void notifyResetting(ServerPlayer player) {
		player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 72000, 0));

		player.connection.send(new ClientboundSetTitleTextPacket(
			Component.empty()
		));
		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Component.literal("Resetting...").withColor(TextColor.RED)
		));
	}

	public static void clearEntities(ServerLevel level) {
		List<Entity> entities = new ArrayList<>();
		level.getAllEntities().forEach(entities::add);

		for (Entity entity : entities) {
			if (entity instanceof ServerPlayer || entity instanceof Villager || entity instanceof Mannequin) continue;

			entity.discard();
		}
	}

	public static void resetMap(MinecraftServer server) {
		if (server.getPlayerList().getPlayers().isEmpty()) return;
		ServerPlayer player = server.getPlayerList().getPlayers().get(0);

		OldBrokenHaybales.getData(server).set(player.getStats().getValue(Stats.BLOCK_MINED.get(Blocks.HAY_BLOCK)));
		SoldWheat.getData(server).set(false);
		SoldSeeds.getData(server).set(false);
		SoldPotatoes.getData(server).set(false);

		ServerLevel theHubTemplate = server.getLevel(Dimensions.THE_HUB_TEMPLATE);
		ServerLevel theHub = server.getLevel(Dimensions.THE_HUB);
		
		BlockPos corner1 = new BlockPos(-166, 50, -285);
		BlockPos corner2 = new BlockPos(325, 270, -755);

		clearEntities(theHub);

		for (BlockPos blockPos : BlockPos.betweenClosed(corner1, corner2)) {
			BlockState templateBlockState = theHubTemplate.getBlockState(blockPos);
			BlockState hubBlockState = theHub.getBlockState(blockPos);
			BlockEntity hubBlockEntity = theHub.getBlockEntity(blockPos);

			if (templateBlockState == hubBlockState && hubBlockEntity == null) continue;

			if (hubBlockEntity != null) {
				theHub.removeBlockEntity(blockPos);
			}

			theHub.setBlock(blockPos, templateBlockState, 3);
		}

		clearEntities(theHub);
	}

	private static void beforeReset(MinecraftServer server, ServerPlayer player) {
		if (!InHubChallenge.getData(player.level().getServer()).get()) return;

		resetting = true;

		TeleportTransition newTransition = new TeleportTransition(
			player.level().getServer().getLevel(Dimensions.THE_HUB),
			new Vec3(120.5, 115, -597.5),
			Vec3.ZERO,
			180f,
			90f,
			TeleportTransition.DO_NOTHING
		);

		RespawnConfig newRespawnConfig = new RespawnConfig(
			new RespawnData(
				new GlobalPos(
					Dimensions.THE_HUB,
					new BlockPos(119, 129, -576)
				), 
				180f, 
				0f
			), 
			true
		);

		player.teleport(newTransition);
		player.setRespawnPosition(newRespawnConfig, false);

		player.setGameMode(GameType.ADVENTURE);

		notifyResetting(player);
	}

	public static void resetInventory(ServerPlayer player) {
		player.closeContainer();
		player.getInventory().clearContent();

		Map<Integer, ItemStack> oldInventory = TheHubEnterInventory.getData(player.level().getServer()).get();
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (!oldInventory.containsKey(i)) continue;
			player.getInventory().setItem(i, oldInventory.get(i).copy());
		}
	}

	private static void actuallyReset(MinecraftServer server, ServerPlayer player) {
		resetMap(server);

		resetInventory(player);

		PausedChallenge.getData(server).set(true);
		HubExitTime.getData(server).set(timeLimit);
			
		notifyFinishedReset(player);
		player.setGameMode(GameType.SURVIVAL);
		resetting = false;
	}

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(
			ResetSplitPayload.TYPE,
			ResetSplitPayload.CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
			PressPayload.TYPE, 
			PressPayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(ResetSplitPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			MinecraftServer server = player.level().getServer();

			beforeReset(server, player);

			if (!InHubChallenge.getData(server).get()) return;
			HubExitTime.getData(server).set(0);
		});

		ServerPlayNetworking.registerGlobalReceiver(PressPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			MinecraftServer server = player.level().getServer();

			PausedChallenge.getData(server).set(false);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getPlayerList().getPlayers().isEmpty()) return;
			ServerPlayer player = server.getPlayerList().getPlayers().get(0);

			if (InHubChallenge.getData(server).get() == false || !player.level().dimension().equals(Dimensions.THE_HUB)) return;

			if (PausedChallenge.getData(server).get() == true) {
				player.connection.send(new ClientboundSetActionBarTextPacket(
					Component.literal("Press any key or mouse button to start").withColor(TextColor.GREEN)
				));
				return;
			}

			if (HubExitTime.getData(server).get() == 1) {
				beforeReset(server, player);
			}

			if (HubExitTime.getData(server).get() <= 0) {
				actuallyReset(server, player);
			}

			int time = HubExitTime.getData(server).get() - 1;

			HubExitTime.getData(server).set(time);

			player.connection.send(new ClientboundSetActionBarTextPacket(
				Component.literal(String.format(
					"%02d:%02d:%02d",
					time / 20 / 60,
					(time / 20) % 60,
					time % 20
				)).withColor(TextColor.GREEN)
			));
		});
	}
}
