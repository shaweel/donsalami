package me.shaweel.donsalami.splits;

import org.jetbrains.annotations.Nullable;

import me.shaweel.donsalami.worldData.CurrentSplit;
import net.minecraft.server.MinecraftServer;

public class SplitImplementation {
	public static String getCurrentSplit(MinecraftServer server) {
		return CurrentSplit.getData(server).get();
	}

	public static @Nullable String getNextSplit(MinecraftServer server) {
		Split nextSplit = Split.valueOf(CurrentSplit.getData(server).get()).next();
		return nextSplit == null ? null : nextSplit.name();
	}

	public static void advanceSplit(MinecraftServer server) {
		String nextSplit = getNextSplit(server);
		CurrentSplit.getData(server).set(nextSplit);
	}

	public static void reset(Split split) {
		//TODO reset the split (kill player)
		split.onReset.run();
	}

	public static void start(Split split) {
		//TODO start the split (set respawn, inv, etc...)
		split.onStart.run();
	}
}
