package me.shaweel.donsalami.the_game;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Elytra {
	private static final double[] x1 = new double[]{400126, 400148, 400197, 400226, 400227, 400228, 400230, 400230, 400230, 400230, 400251, 400288, 400270, 400248, 400211, 400181};
	private static final double[] y1 = new double[]{297, 293, 282, 280, 275, 270, 265, 260, 255, 251, 224, 210, 194, 175, 158, 140};
	private static final double[] z1 = new double[]{399914, 399908, 399890, 399898, 399898, 399897, 399898, 399898, 399898, 399898, 399899, 399920, 399959, 399988, 399967, 399949};
	private static final double[] x2 = new double[]{400127, 400149, 400198, 400232, 400232, 400234, 400234, 400234, 400234, 400234, 400252, 400292, 400274, 400247, 400212, 400180};
	private static final double[] y2 = new double[]{301, 297, 286, 281, 276, 271, 266, 261, 256, 251, 228, 214, 198, 179, 162, 148};
	private static final double[] z2 = new double[]{399918, 399912, 399894, 399902, 399902, 399902, 399902, 399902, 399902, 399902, 399903, 399921, 399958, 399992, 399972, 399953};
	private static boolean[] beenIn = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
	private static boolean fucked = false;
	private static boolean initialized = false;

	private static double lastX = 0;
	private static double lastY = 0;
	private static double lastZ = 0;

	public static void reset() {
		beenIn = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
	}

	private static boolean lineIntersectsBox(
		double x0, double y0, double z0,
		double x1, double y1, double z1,
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ
	) {
		double dx = x1 - x0;
		double dy = y1 - y0;
		double dz = z1 - z0;

		double tMin = 0.0;
		double tMax = 1.0;

		if (dx == 0) {
			if (x0 < minX || x0 > maxX) return false;
		} else {
			double t1 = (minX - x0) / dx;
			double t2 = (maxX - x0) / dx;
			if (t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}
			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		if (dy == 0) {
			if (y0 < minY || y0 > maxY) return false;
		} else {
			double t1 = (minY - y0) / dy;
			double t2 = (maxY - y0) / dy;
			if (t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}
			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		if (dz == 0) {
			if (z0 < minZ || z0 > maxZ) return false;
		} else {
			double t1 = (minZ - z0) / dz;
			double t2 = (maxZ - z0) / dz;
			if (t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}
			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		return true;
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getPlayerList().getPlayers().isEmpty()) return;
			ServerPlayer player = server.getPlayerList().getPlayers().get(0);

			double x = player.getX();
			double y = player.getY();
			double z = player.getZ();

			if (!initialized) {
				lastX = x;
				lastY = y;
				lastZ = z;
				initialized = true;
			}

			for (int i = 0; i < x1.length; i++) {
				if (lineIntersectsBox(
						lastX, lastY, lastZ,
						x, y, z,
						x1[i], y1[i], z1[i],
						x2[i], y2[i], z2[i]
					) 
					&& beenIn[i] == false 
				) {
					beenIn[i] = true;
					player.connection.send(new ClientboundSoundPacket(
						Holder.direct(SoundEvents.ARROW_HIT_PLAYER),
						SoundSource.PLAYERS,
						player.getX(),
						player.getY(),
						player.getZ(),
						1.0F,
						1.5F,
						player.getRandom().nextLong()
					));
					player.connection.send(new ClientboundSetActionBarTextPacket(
						Component.literal("Ring " + (i+1) + " completed!").withColor(TextColor.GREEN)
					));
				}
			}

			lastX = x;
			lastY = y;
			lastZ = z;

			if (x >= 399000 && x <= 401000 && 
				z >= 399000 && z <= 401000 &&
				y < 144
			) {
				fucked = false;
				for (boolean been : beenIn) { 
					if (been) continue;
					
					fucked = true;

					beenIn = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

					player.setHealth(0);
					player.connection.send(new ClientboundSetActionBarTextPacket(
						Component.literal("You must go through all rings to continue").withColor(TextColor.RED)
					));
					break;
				}

				if (!fucked) {
					player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
				}
			}
		});
	}
}
