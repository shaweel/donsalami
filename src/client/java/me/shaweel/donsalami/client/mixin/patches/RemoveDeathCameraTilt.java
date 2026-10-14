package me.shaweel.donsalami.client.mixin.patches;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;

@Mixin(GameRenderer.class)
public class RemoveDeathCameraTilt {
	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	private void removeDeathCameraTilt(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo callbackInfo) {
		if (cameraState.entityRenderState.isLiving && cameraState.entityRenderState.isDeadOrDying) {
			callbackInfo.cancel();
		}
	}
}
