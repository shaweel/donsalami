package me.shaweel.donsalami.network;

import me.shaweel.donsalami.cutscene.Cutscene;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CutscenePayload(Cutscene cutscene) implements CustomPacketPayload {
	public static final Type<CutscenePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("donsalami", "cutscene"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CutscenePayload> CODEC = StreamCodec.composite(
		Cutscene.CODEC,
		CutscenePayload::cutscene,

		CutscenePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}