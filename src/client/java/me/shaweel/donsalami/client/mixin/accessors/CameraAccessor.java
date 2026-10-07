package me.shaweel.donsalami.client.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;

@Mixin(Camera.class)
public interface CameraAccessor {
	@Invoker("setPosition")
	void invokeSetPosition(double x, double y, double z);

	@Invoker("setRotation")
	void invokeSetRotation(float yRot, float xRot);
}
