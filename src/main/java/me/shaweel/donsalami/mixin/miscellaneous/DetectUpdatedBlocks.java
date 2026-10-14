package me.shaweel.donsalami.mixin.miscellaneous;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

import me.shaweel.donsalami.utils.Log;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LevelWriter.class)
public class DetectUpdatedBlocks {
	@Inject(method = "setBlockAndUpdate", at = @At("HEAD"))
	private void detectUpdatedBlock(BlockPos blockPos, BlockState blockState) {
		Log.debug(
			Component.literal("Block changed at: ").withStyle(ChatFormatting.GRAY)
			.append(String.format("X: %s, Y: %s, Z: %s", blockPos.getX(), blockPos.getY(), blockPos.getZ())).withStyle(ChatFormatting.WHITE)
			.append(" to ").withStyle(ChatFormatting.GRAY)
			.append(blockState.getBlock().getName().getString()).withStyle(ChatFormatting.WHITE)
		);
	}
}
