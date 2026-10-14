package me.shaweel.donsalami.splits;

import java.util.ArrayList;
import java.util.List;
import me.shaweel.donsalami.DonSalami;
import me.shaweel.donsalami.cutscene.Cutscene;
import me.shaweel.donsalami.network.CutscenePayload;
import me.shaweel.donsalami.the_game.the_hub.TheHubChallenge;
import me.shaweel.donsalami.utils.Utils;
import me.shaweel.donsalami.worldData.HubExitTime;
import me.shaweel.donsalami.worldData.InHubChallenge;
import me.shaweel.donsalami.worldData.PausedChallenge;
import me.shaweel.donsalami.worldData.OldBrokenHaybales;
import me.shaweel.donsalami.worldData.SoldWheat;
import me.shaweel.donsalami.worldData.SoldSeeds;
import me.shaweel.donsalami.worldData.SoldPotatoes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class IndividualSplits {
	private static List<Item> HUB_BLACKLIST;
	private static List<Item> HUB_REQUIRED_ITEMS;

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

	private static void unenchantEverything(ServerPlayer player) {
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

	private static void deleteHubBlacklistedItems(ServerPlayer player) {
		List<Item> items = hubBlacklist();
		Inventory inv = player.getInventory();
		List<String> deleted = new ArrayList<>();

		for (Item item : items) {
			if (!inv.contains(stack -> stack.is(item))) continue;

			deleted.add(item.getName(item.getDefaultInstance()).getString());

			inv.clearOrCountMatchingItems(
				stack -> stack.is(item),
				false,
				Integer.MAX_VALUE,
				inv
			);
		}

		if (deleted.isEmpty()) return;

		player.sendSystemMessage(Component.literal("The following items have been removed from your inventory to prevent cheesing: ").withColor(TextColor.RED)
		.append(Component.literal(String.join(", ", deleted)).withColor(TextColor.WHITE)));
	}

	private static void giveHubRequiredItems(ServerPlayer player) {
		List<Item> items = hubRequiredItems();
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
	
	private static void awardHubExitTimeAdvancements(ServerPlayer player, MinecraftServer server, int completionTime) {
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

	private static void awardHubExitChallengeAdvancements(ServerPlayer player, MinecraftServer server) {
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

	public static void baseOverworldBeforeAdvance(MinecraftServer server, ServerPlayer player) {
		player.getInventory().clearContent();
		server.overworld().clockManager().setTotalTicks(
			server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD), 0
		);
	}

	public static void elytraBeforeAdvance(MinecraftServer server, ServerPlayer player) {
		baseOverworldBeforeAdvance(server, player);
		player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
		player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1);
	}

	public static void parkourBeforeAdvance(MinecraftServer server, ServerPlayer player) {
		player.setGameMode(GameType.ADVENTURE);

		unenchantEverything(player);
		deleteHubBlacklistedItems(player);
		giveHubRequiredItems(player);

		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "function donsalami:enablescoreboard");
	}

	public static void parkourAfterAdvance(MinecraftServer server, ServerPlayer player) {
		player.connection.send(new ClientboundSetTitleTextPacket(
			Component.literal("Tidy your inventory!").withColor(TextColor.BLUE)
		));

		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Component.literal("This is extremely important!").withColor(TextColor.RED)
		));
	}

	public static void hubBeforeAdvance(MinecraftServer server, ServerPlayer player) {
		player.setGameMode(GameType.SURVIVAL);
		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "function donsalami:disablescoreboard");

		HubExitTime.getData(server).set(TheHubChallenge.timeLimit);
		InHubChallenge.getData(server).set(true);
		PausedChallenge.getData(server).set(true);
	}

	public static void hubAfterAdvance(MinecraftServer server, ServerPlayer player) {
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
	}

	public static void netherAfterAdvance(MinecraftServer server, ServerPlayer player) {
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

		awardHubExitTimeAdvancements(player, server, completionTime);
		awardHubExitChallengeAdvancements(player, server);
	}

	public static void ruinedPortalBeforeAdvance(MinecraftServer server, ServerPlayer player) {
		baseOverworldBeforeAdvance(server, player);
		
		player.setGameMode(GameType.SURVIVAL);
		server.getWorldData().setAllowCommands(false);
	}
}
