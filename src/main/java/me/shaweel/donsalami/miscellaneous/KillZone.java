package me.shaweel.donsalami.miscellaneous;

import me.shaweel.donsalami.recoursekeys.Dimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.Entity;

public class KillZone {
	public static void initialize() {
		ServerTickEvents.END_LEVEL_TICK.register(level -> {
			if (!level.dimension().equals(Dimensions.TIDY_INVENTORY)) return;
			
			for (Entity entity : level.getAllEntities()) {
				if (entity == null || entity.getY() >= 95) continue;

				entity.kill(level);
			}
		});
	}
}
