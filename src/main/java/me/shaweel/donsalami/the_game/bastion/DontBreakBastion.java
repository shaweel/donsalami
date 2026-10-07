package me.shaweel.donsalami.the_game.bastion;

import java.util.Set;

import me.shaweel.donsalami.recoursekeys.Structures;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;

public class DontBreakBastion {
	private static final Set<Block> PROTECTED_BLOCKS = Set.of(
		Blocks.BLACKSTONE,
		Blocks.POLISHED_BLACKSTONE_BRICKS,
		Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
		Blocks.CHISELED_POLISHED_BLACKSTONE,
		Blocks.GILDED_BLACKSTONE,
		Blocks.BLACKSTONE_SLAB,
		Blocks.POLISHED_BLACKSTONE_SLAB,
		Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
		Blocks.BLACKSTONE_STAIRS,
		Blocks.POLISHED_BLACKSTONE_STAIRS,
		Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS,
		Blocks.CRIMSON_SIGN,
		Blocks.PIGLIN_HEAD,
		Blocks.CRIMSON_FUNGUS,
		Blocks.NETHER_WART,
		Blocks.SOUL_SOIL,
		Blocks.SOUL_SAND,
		Blocks.LODESTONE,
		Blocks.BASALT,
		Blocks.CRACKED_DEEPSLATE_TILES,
		Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
		Blocks.POLISHED_BLACKSTONE_WALL,
		Blocks.MAGMA_BLOCK,
		Blocks.SOUL_LANTERN,
		Blocks.IRON_CHAIN,
		Blocks.IRON_BARS
	);

	private static boolean inBastion(Level level, BlockPos position) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return false;
		}
		
		StructureManager structureManager = serverLevel.structureManager();

		Registry<Structure> structures = structureManager.registryAccess().lookupOrThrow(Registries.STRUCTURE);

		Structure bastion = structures.getOrThrow(Structures.DUNGEON_BASTION).value();

		return structureManager.getStructureAt(position, bastion).isValid();
	}

	public static boolean isProtected(Level level, BlockPos position) {
		return (PROTECTED_BLOCKS.contains(level.getBlockState(position).getBlock()) && inBastion(level, position));
	} 

	public static void initialize() {
		PlayerBlockBreakEvents.BEFORE.register((level, player, position, blockState, blockEntity) -> {
			if (isProtected(level, position)) {
				return false;
			}

			return true;
		});
	}
}
