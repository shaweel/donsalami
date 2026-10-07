package me.shaweel.donsalami.client.render;

import java.util.ArrayList;
import java.util.List;

import me.shaweel.donsalami.network.CutscenePayload;
import me.shaweel.donsalami.client.mixin.accessors.CameraAccessor;
import me.shaweel.donsalami.cutscene.Cutscene;
import me.shaweel.donsalami.cutscene.Cutscene.Keyframe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public class CutsceneRenderer {
	public static final List<Cutscene> CUTSCENES = new ArrayList<>();

	private static void render(Cutscene cutscene) {
		System.out.println("RENDERING CUTSCENE");

		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		CameraAccessor camera = (CameraAccessor) minecraft.gameRenderer.mainCamera();		

		Keyframe keyframe = cutscene.keyframes.get(cutscene.currentKeyframe);
		Keyframe lastKeyframe;
		
		if (cutscene.currentKeyframe > 0) {
			lastKeyframe = cutscene.keyframes.get(cutscene.currentKeyframe - 1);
		} else {
			lastKeyframe = new Keyframe(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), 0);
		}

		long sinceLastKeyframe = cutscene.getElapsedMs();

		for (int i = 0; i < cutscene.currentKeyframe; i++) {
			sinceLastKeyframe -= cutscene.keyframes.get(i).durationMs();
		}

		if (sinceLastKeyframe > keyframe.durationMs()) {
			cutscene.currentKeyframe++;
			if (cutscene.currentKeyframe >= cutscene.keyframes.size()) cutscene.stop();
		}

		float progress;

		if (keyframe.durationMs() > 0) {
			progress = Mth.clamp((float) sinceLastKeyframe / keyframe.durationMs(), 0f, 1f);
		} else {
			progress = 1f;
		}

		double currentX = Mth.lerp(progress, lastKeyframe.x(), keyframe.x());
		double currentY = Mth.lerp(progress, lastKeyframe.y(), keyframe.y());
		double currentZ = Mth.lerp(progress, lastKeyframe.z(), keyframe.z());
		float currentYRot = Mth.lerp(progress, lastKeyframe.yRot(), keyframe.yRot());
		float currentXRot = Mth.lerp(progress, lastKeyframe.xRot(), keyframe.xRot());

		camera.invokeSetPosition(currentX, currentY, currentZ);
		camera.invokeSetRotation(currentYRot, currentXRot);
	}

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(
			CutscenePayload.TYPE,
			CutscenePayload.CODEC
		);

		ClientPlayNetworking.registerGlobalReceiver(CutscenePayload.TYPE, (payload, context) -> {
			System.out.println("RECEIVED CUTSCENE");
			
			Cutscene cutscene = payload.cutscene();

			if (!CUTSCENES.contains(cutscene)) {
				CUTSCENES.add(cutscene);
			}

			cutscene.start();
			System.out.println("STARTING CUTSCENE");
		});
		
		LevelRenderEvents.START_MAIN.register(context -> {
			for (Cutscene cutscene : CUTSCENES) {
				if (cutscene.isPlaying()) render(cutscene);
			}
		});
	}
}
