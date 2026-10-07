package me.shaweel.donsalami.healing;

import java.util.HashMap;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

public class FoodHealing {
	private final static int HEAL_TICK = 1;
	private final static float PER_HEAL_TICK = 0.1f;

	public static HashMap<UUID,Integer> toHeal = new HashMap<>();
	private static HashMap<UUID,Integer> sinceLastHeal = new HashMap<>();

	private static void actuallyHeal(int playerSinceLastHeal, int playerToHeal, Player player) {
		if (playerToHeal <= 0) return;
		
		if (playerSinceLastHeal > 0) {
			sinceLastHeal.put(player.getUUID(), playerSinceLastHeal-1);
			return;
		}

		int healOnce = Math.max(Math.round((playerToHeal * PER_HEAL_TICK)), 1);

		toHeal.put(player.getUUID(), playerToHeal - healOnce);
		player.heal(healOnce);
		sinceLastHeal.put(player.getUUID(), HEAL_TICK);
	}

	private static void heal(Player player) {
		if (!player.isAlive()) return;
		
		int playerSinceLastHeal = sinceLastHeal.getOrDefault(player.getUUID(), -1);
		int playerToHeal = toHeal.getOrDefault(player.getUUID(), -1);

		if (playerSinceLastHeal == -1 || playerToHeal == -1) {
			System.err.println("Attempted to heal invalid player");
			return;
		}
		
		actuallyHeal(playerSinceLastHeal, playerToHeal, player);
	}
	
	public static void addHealToQueue(Player player, int amount) {
		System.out.println(String.format("adding food %s", amount));

		toHeal.put(player.getUUID(), toHeal.get(player.getUUID()) + amount);
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Player player : server.getPlayerList().getPlayers()) {
				heal(player);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Player player = handler.player;

			toHeal.put(player.getUUID(), 0);
			sinceLastHeal.put(player.getUUID(), 0);
		});
	}
}
