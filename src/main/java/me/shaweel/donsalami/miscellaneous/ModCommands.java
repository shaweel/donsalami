package me.shaweel.donsalami.miscellaneous;

import com.mojang.brigadier.CommandDispatcher;

import me.shaweel.donsalami.splits.SplitImplementation;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ModCommands {
	private static void registerCreateSplitSnapshotsCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("create_split_snapshots").executes(context -> {
			SplitImplementation.createSplitSnapshots(context.getSource().getServer(), context.getSource().getPlayer());
			context.getSource().sendSuccess(
				() -> Component.literal("Successfuly created split snapshots").withStyle(ChatFormatting.GREEN), false
			);
			return 1;
		}));
	}

	public static void initialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
				registerCreateSplitSnapshotsCommand(dispatcher);
			}
		});
	}
}
