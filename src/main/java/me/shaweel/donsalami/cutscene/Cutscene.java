package me.shaweel.donsalami.cutscene;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Cutscene {
	public record Keyframe(double x, double y, double z, float yRot, float xRot, int durationMs) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Keyframe> CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE,
			Keyframe::x,

			ByteBufCodecs.DOUBLE,
			Keyframe::y,

			ByteBufCodecs.DOUBLE,
			Keyframe::z,

			ByteBufCodecs.FLOAT,
			Keyframe::yRot,

			ByteBufCodecs.FLOAT,
			Keyframe::xRot,

			ByteBufCodecs.VAR_INT,
			Keyframe::durationMs,

			Keyframe::new
		);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, Cutscene> CODEC = 
		Keyframe.CODEC
		.apply(ByteBufCodecs.list())
		.map(Cutscene::new, cutscene -> cutscene.keyframes);	
	

	public final List<Keyframe> keyframes = new ArrayList<>();

	public int currentKeyframe = -1;
	public long startTime = 0;

	public Cutscene(List<Keyframe> keyframes) {
		this.keyframes.addAll(keyframes);
	}

	public Cutscene(Keyframe startKeyframe) {
		this.keyframes.add(startKeyframe);
	}

	public Cutscene(double startX, double startY, double startZ, float startYRot, float startXRot, int durationMs) {
		this.keyframes.add(new Keyframe(startX, startY, startZ, startYRot, startXRot, durationMs));
	}

	public Cutscene addKeyframe(double x, double y, double z, float yRot, float xRot, int durationMs) {
		this.keyframes.add(new Keyframe(x, y, z, yRot, xRot, durationMs));
		return this;
	}

	public Cutscene addKeyframe(Keyframe keyframe) {
		this.keyframes.add(keyframe);
		return this;
	}

	public Cutscene addPause(int durationMs) {
		if (this.keyframes.isEmpty()) {
			throw new IllegalStateException("Cannot add a pause with no Keyframes");
		}

		Keyframe lastKeyframe = this.keyframes.get(this.keyframes.size() - 1);

		this.keyframes.add(new Keyframe(lastKeyframe.x, lastKeyframe.y, lastKeyframe.z, lastKeyframe.yRot, lastKeyframe.xRot, durationMs));
		return this;
	}

	public void start() {
		this.currentKeyframe = 0;
		this.startTime = System.nanoTime();
	}

	public void stop() {
		this.currentKeyframe = -1;
	}

	public boolean isPlaying() {
		return this.currentKeyframe > -1;
	}

	public long getElapsedMs() {
		return (System.nanoTime() - this.startTime) / 1_000_000;
	}
}
