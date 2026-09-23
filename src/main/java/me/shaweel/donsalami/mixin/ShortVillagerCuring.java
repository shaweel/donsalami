package me.shaweel.donsalami.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;

@Mixin(ZombieVillager.class)
public class ShortVillagerCuring {
	private static final int curingTimeTicks = 100;

	@Shadow
	private void startConverting(UUID player, int time) {}

	@Inject(at = @At("HEAD"), method = "startConverting", cancellable = true)
	private void shorterCuring(UUID player, int time, CallbackInfo callbackInfo) {
		if (time <= curingTimeTicks) return;
		startConverting(player, curingTimeTicks);

		callbackInfo.cancel();
	}

	@Inject(at = @At("HEAD"), method = "finishConversion")
	private void feinberg(final ServerLevel level, CallbackInfo callbackInfo) {
		ZombieVillager zombieVillager = (ZombieVillager)(Object)this;

		level.playSound(
			null,
			zombieVillager.blockPosition(),
			SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("donsalami", "feinberg")),
			SoundSource.NEUTRAL,
			1.0F,
			1.0F
		);

		MinecraftServer server = level.getServer();
		if (server.getPlayerList().getPlayers().isEmpty()) return;
		ServerPlayer player = server.getPlayerList().getPlayers().get(0);

		player.sendSystemMessage(Component.literal("[Feinberg]: ").withColor(TextColor.DARK_PURPLE)
		.append(Component.literal("yo btw there's a buried treasure full of emeralds somewhere near here").withColor(TextColor.WHITE)));
	}
}
