package me.shaweel.donsalami.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.stats.Stats;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData.RespawnData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.donsalami.DonSalami;
import me.shaweel.donsalami.miscellaneous.Cutscene;
import me.shaweel.donsalami.miscellaneous.TheHubChallenge;
import me.shaweel.donsalami.network.CutscenePayload;
import me.shaweel.donsalami.recoursekeys.Dimensions;
import me.shaweel.donsalami.utils.Utils;
import me.shaweel.donsalami.worldData.InHubChallenge;
import me.shaweel.donsalami.worldData.NetherEnters;
import me.shaweel.donsalami.worldData.HubExitTime;
import me.shaweel.donsalami.worldData.OldBrokenHaybales;
import me.shaweel.donsalami.worldData.PausedChallenge;
import me.shaweel.donsalami.worldData.SoldPotatoes;
import me.shaweel.donsalami.worldData.SoldSeeds;
import me.shaweel.donsalami.worldData.SoldWheat;
import me.shaweel.donsalami.worldData.TheHubEnterInventory;

@Mixin(NetherPortalBlock.class)
public class NetherEnter {
	private static List<Item> HUB_BLACKLIST;
	private static List<Item> HUB_REQUIRED_ITEMS;

	private static final int[] x = new int[]{400087, 299896, 199797, 99952, -412};
	private static final int[] y = new int[]{324, 94, 65, 63, 66};
	private static final int[] z = new int[]{399917, 300019, 200272, 100095, 234};

	private static List<Item> hubBlacklist() {
		if (HUB_BLACKLIST == null) {
			HUB_BLACKLIST = List.of(
				Items.OBSIDIAN, Items.WHEAT, Items.WHEAT_SEEDS, Items.HAY_BLOCK, Items.EMERALD_BLOCK, Items.EMERALD, Items.POTATO, Items.BAKED_POTATO,
				Items.COBBLESTONE, Items.COBBLED_DEEPSLATE, Items.BLACKSTONE, Items.BUNDLE, Items.FURNACE, Items.SMOKER, Items.OAK_LOG, Items.BIRCH_LOG,
				Items.SPRUCE_LOG, Items.DARK_OAK_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG, Items.MANGROVE_LOG, Items.CHERRY_LOG, Items.PALE_OAK_LOG,
				Items.CRIMSON_HYPHAE, Items.WARPED_HYPHAE, Items.STRIPPED_OAK_LOG, Items.STRIPPED_BIRCH_LOG, Items.STRIPPED_SPRUCE_LOG, 
				Items.STRIPPED_DARK_OAK_LOG, Items.STRIPPED_JUNGLE_LOG, Items.STRIPPED_ACACIA_LOG, Items.STRIPPED_MANGROVE_LOG, Items.STRIPPED_CHERRY_LOG, 
				Items.STRIPPED_PALE_OAK_LOG, Items.STRIPPED_CRIMSON_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, Items.OAK_WOOD, Items.BIRCH_WOOD, Items.SPRUCE_WOOD, 
				Items.DARK_OAK_WOOD, Items.JUNGLE_WOOD, Items.ACACIA_WOOD, Items.MANGROVE_WOOD, Items.PALE_OAK_WOOD, Items.CHERRY_WOOD, Items.STRIPPED_OAK_WOOD, 
				Items.STRIPPED_BIRCH_WOOD, Items.STRIPPED_SPRUCE_WOOD, Items.STRIPPED_DARK_OAK_WOOD, Items.STRIPPED_JUNGLE_WOOD, Items.STRIPPED_ACACIA_WOOD, 
				Items.STRIPPED_MANGROVE_WOOD, Items.STRIPPED_PALE_OAK_WOOD, Items.STRIPPED_CHERRY_WOOD, Items.BUNDLE, Items.DYED_BUNDLE.black(), 
				Items.DYED_BUNDLE.white(), Items.DYED_BUNDLE.lightGray(), Items.DYED_BUNDLE.gray(), Items.DYED_BUNDLE.blue(), Items.DYED_BUNDLE.lightBlue(), 
				Items.DYED_BUNDLE.cyan(), Items.DYED_BUNDLE.green(), Items.DYED_BUNDLE.lime(), Items.DYED_BUNDLE.red(), Items.DYED_BUNDLE.orange(), 
				Items.DYED_BUNDLE.yellow(), Items.DYED_BUNDLE.pink(), Items.DYED_BUNDLE.purple(), Items.DYED_BUNDLE.magenta(), Items.CAMPFIRE, Items.COAL, 
				Items.CHARCOAL, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE, Items.ENDER_PEARL, Items.SHULKER_BOX, Items.SOUL_CAMPFIRE, 
				Items.SOUL_SAND, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE, Items.ENCHANTED_BOOK, Items.ENCHANTING_TABLE, Items.ANVIL, 
				Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE, Items.SMITHING_TABLE, Items.DIAMOND, Items.GOLD_INGOT, Items.GOLD_NUGGET,
				Items.GOLD_BLOCK, Items.DIAMOND_BLOCK, Items.SAND, Items.GUNPOWDER, Items.TNT, Items.TNT_MINECART
			);
		}

		return HUB_BLACKLIST;
	}

	private static List<Item> hubRequiredItems() {
		if (HUB_REQUIRED_ITEMS == null) {
			HUB_REQUIRED_ITEMS = List.of(
				Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_HOE, Items.FLINT_AND_STEEL, Items.CRAFTING_TABLE
			);
		}
		
		return HUB_REQUIRED_ITEMS;
	}

	private void tryUnenchant(ServerPlayer player) {
		Inventory inv = player.getInventory();

		List<String> unenchanted = new ArrayList<>();

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);

			if (stack.isEmpty() || !EnchantmentHelper.hasAnyEnchantments(stack)) continue;
			
			stack.remove(DataComponents.ENCHANTMENTS);
			stack.remove(DataComponents.STORED_ENCHANTMENTS);

			unenchanted.add(stack.getItemName().getString());
		}

		if (unenchanted.isEmpty()) return;

		player.sendSystemMessage(Component.literal("All enchanted items in your inventory have been unenchanted to prevent cheesing, these are: ").withColor(TextColor.RED)
		.append(Component.literal(String.join(", ", unenchanted)).withColor(TextColor.WHITE)));
	}

	private void tryDelete(List<Item> items, ServerPlayer player) {
		Inventory inv = player.getInventory();

		List<String> deleted = new ArrayList<>();

		for (Item item : items) {
			if (!inv.contains(stack -> stack.is(item))) continue;

			deleted.add(item.getName(item.getDefaultInstance()).getString());

			inv.clearOrCountMatchingItems(
				stack -> stack.is(item),
				Integer.MAX_VALUE,
				inv
			);
		}

		if (deleted.isEmpty()) return;

		player.sendSystemMessage(Component.literal("The following items have been removed from your inventory to prevent cheesing: ").withColor(TextColor.RED)
		.append(Component.literal(String.join(", ", deleted)).withColor(TextColor.WHITE)));
	}

	private void tryGive(List<Item> items, ServerPlayer player) {
		Inventory inv = player.getInventory();

		List<String> given = new ArrayList<>();

		for (Item item : items) {
			if (inv.contains(stack -> stack.is(item))) continue;

			given.add(item.getName(item.getDefaultInstance()).getString());
			player.getInventory().add(item.getDefaultInstance());
		}

		given.add("64 Spruce Planks");
		player.getInventory().add(new ItemStack(Items.SPRUCE_PLANKS, 64));

		player.sendSystemMessage(Component.literal("The following items have been given to you to prevent softlocking: ").withColor(TextColor.GREEN)
		.append(Component.literal(String.join(", ", given)).withColor(TextColor.WHITE)));
	}

	private void awardTimeAdvancements(ServerPlayer player, MinecraftServer server, int completionTime) {
		AdvancementHolder advancement55 = server.getAdvancements().get(Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "hub/time/55s"));
		AdvancementHolder advancement45 = server.getAdvancements().get(Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "hub/time/55s"));
		AdvancementHolder advancement40 = server.getAdvancements().get(Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "hub/time/55s"));
		
		if (completionTime < 55*20) {
			player.getAdvancements().award(advancement55, "code_only");
		}

		if (completionTime < 45*20) {
			player.getAdvancements().award(advancement45, "code_only");
		}

		if (completionTime < 40*20) {
			player.getAdvancements().award(advancement40, "code_only");
		}
	}

	private void awardChallengeAdvancements(ServerPlayer player, MinecraftServer server) {
		AdvancementHolder noHayBales = server.getAdvancements().get(Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "hub/time/nohay"));
		AdvancementHolder seedsOnly = server.getAdvancements().get(Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "hub/time/seeds"));
		AdvancementHolder potatoesOnly = server.getAdvancements().get(Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "hub/time/potatoes"));
		


		if (player.getStats().getValue(Stats.BLOCK_MINED.get(Blocks.HAY_BLOCK)) == OldBrokenHaybales.getData(server).get()) {
			player.getAdvancements().award(noHayBales, "code_only");
		}
		if (SoldSeeds.getData(server).get() && !SoldWheat.getData(server).get() && !SoldPotatoes.getData(server).get()) {
			player.getAdvancements().award(seedsOnly, "code_only");
		}
		
		if (SoldPotatoes.getData(server).get() && !SoldWheat.getData(server).get() && !SoldSeeds.getData(server).get()) {
			player.getAdvancements().award(potatoesOnly, "code_only");
		}
	}

	private void teleportSetRespawnAndKill(
		ServerPlayer player, MinecraftServer server, ResourceKey<Level> dimension, double x, double y, double z, float yRot, float xRot
	) {
		TeleportTransition newTransition = new TeleportTransition(
			server.getLevel(dimension), new Vec3(x, y, z), Vec3.ZERO, yRot, xRot, TeleportTransition.DO_NOTHING
		);

		RespawnConfig newRespawnConfig = new RespawnConfig(
			new RespawnData(
				new GlobalPos(dimension, new BlockPos((int) x, (int) y, (int) z)), 
				yRot, xRot
			), 
			true
		);

		player.teleport(newTransition);
		player.setRespawnPosition(newRespawnConfig, false);
		player.setHealth(0);
	}

	private void exitTheHub(ServerPlayer player, MinecraftServer server) {
		TheHubChallenge.resetInventory(player);

		player.connection.send(new ClientboundSetTitleTextPacket(
			Component.literal("Good job").withColor(TextColor.GREEN)
		));

		HubExitTime hubExitTime = HubExitTime.getData(server);
		
		int completionTime = TheHubChallenge.timeLimit - hubExitTime.get();

		int ticks = completionTime % 20;
		int seconds = (completionTime / 20) % 60;
		int minutes = completionTime / 20 / 60;

		String completionTimeText = minutes > 0 
			? String.format("%02dm %02ds %02dt", minutes, seconds, ticks)
			: seconds > 0 
				? String.format("%02ds %02dt", seconds, ticks) 
				: String.format("%02dt", ticks);

		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Component.literal(completionTimeText).withColor(TextColor.LIGHT_PURPLE))
		);

		awardTimeAdvancements(player, server, completionTime);
		awardChallengeAdvancements(player, server);
	}

	private void enterTheHub(ServerPlayer player, MinecraftServer server) {
		player.setGameMode(GameType.SURVIVAL);
		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "function donsalami:disablescoreboard");

		teleportSetRespawnAndKill(player, server, Dimensions.THE_HUB, 119, 129, -576, 180, 0);

		Cutscene hypixelSkyblockCutscene = new Cutscene(119d, 175d, -600d, 180f, 70f, 1000)
		.addPause(1500)
		.addKeyframe(119d, 129d, -576d, 180f, 0f, 250);
		
		System.out.println("SENDING CUTSCENE");

		ServerPlayNetworking.send(player, new CutscenePayload(hypixelSkyblockCutscene));

		Utils.freezeFor(50, server);

		player.connection.send(new ClientboundSetTitleTextPacket(
			Component.literal("Enter the nether").withColor(TextColor.DARK_PURPLE)
		));

		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Component.literal("under 1min 5s").withColor(TextColor.RED).append(Component.literal(" or die!").withColor(TextColor.DARK_RED))
		));

		player.sendSystemMessage(Component.literal("[Fun fact]: ").withColor(TextColor.LIGHT_PURPLE)
		.append(Component.literal("there's a keybind to reset this challenge").withColor(TextColor.WHITE)));

		startPortalTimer(player, server);
	}

	private void enterTheParkour(ServerPlayer player, MinecraftServer server) {
		player.setGameMode(GameType.ADVENTURE);

		tryUnenchant(player);
		tryDelete(hubBlacklist(), player);
		tryGive(hubRequiredItems(), player);
			
		player.connection.send(new ClientboundSetTitleTextPacket(
			Component.literal("Tidy your inventory!").withColor(TextColor.BLUE)
		));

		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Component.literal("This is extremely important!").withColor(TextColor.RED)
		));

		teleportSetRespawnAndKill(player, server, Dimensions.TIDY_INVENTORY, 0d, 100d, 0d, 180f, 0f);

		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "function donsalami:enablescoreboard");
	}

	private void startPortalTimer(ServerPlayer player, MinecraftServer server) {
		TheHubChallenge.resetMap(server);
		
		Map<Integer, ItemStack> inventoryToSave = new HashMap<>();
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (player.getInventory().getItem(i).isEmpty()) continue;
			inventoryToSave.put(i, player.getInventory().getItem(i).copy());
		}

		TheHubEnterInventory.getData(server).set(inventoryToSave);
		HubExitTime.getData(server).set(TheHubChallenge.timeLimit);
		InHubChallenge.getData(server).set(true);
		PausedChallenge.getData(server).set(true);
	}

	private void enterNewOverworld(ServerPlayer player, MinecraftServer server, int netherEnters) {
		player.getInventory().clearContent();

		if (netherEnters == 0) {
			player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
			player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1);
		}

		teleportSetRespawnAndKill(player, server, Level.OVERWORLD, x[netherEnters], y[netherEnters], z[netherEnters], -90, 0);

		server.overworld().clockManager().setTotalTicks(server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD), 0);
	}

	@Inject(at = @At("HEAD"), method = "getPortalDestination", cancellable = true)
	private void netherPortal(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos, CallbackInfoReturnable<ServerPlayer> callbackInfoReturnable) {		
		MinecraftServer server = currentLevel.getServer();
		ServerPlayer player = null;

		if (entity instanceof ServerPlayer serverPlayer) {
			player = serverPlayer;
		} else { return; }

		NetherEnters netherEnters = NetherEnters.getData(server);
		int oldNetherEnters = netherEnters.get();

		netherEnters.set(netherEnters.get() + 1);

		if (oldNetherEnters > 6) { return; }
		else if (oldNetherEnters == 6) {
			enterTheHub(player, server);
			callbackInfoReturnable.setReturnValue(null);
			return;
		} else if (oldNetherEnters == 5) {
			enterTheParkour(player, server);
			callbackInfoReturnable.setReturnValue(null);
			return;
		} else if (player.level().dimension().equals(Dimensions.THE_HUB)) {
			exitTheHub(player, server);
			return;
		}

		enterNewOverworld(player, server, oldNetherEnters);

		callbackInfoReturnable.setReturnValue(null);
	}
}