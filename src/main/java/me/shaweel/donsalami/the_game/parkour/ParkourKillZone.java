package me.shaweel.donsalami.the_game.parkour;

import me.shaweel.donsalami.recoursekeys.Dimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.Entity;

public class ParkourKillZone {
	private static final int DEATH_Y_LEVEL = 95;

	public static void initialize() {
		ServerTickEvents.END_LEVEL_TICK.register(level -> {
			if (!level.dimension().equals(Dimensions.TIDY_INVENTORY)) return;
			
			for (Entity entity : level.getAllEntities()) {
				if (entity == null || entity.getY() >= DEATH_Y_LEVEL) continue;

				entity.kill(level);
			}
		});
	}
}
