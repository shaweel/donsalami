package me.shaweel.donsalami.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ResetSplitPayload() implements CustomPacketPayload {
	public static final Type<ResetSplitPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("donsalami", "reset_split"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResetSplitPayload> CODEC = StreamCodec.unit(new ResetSplitPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}