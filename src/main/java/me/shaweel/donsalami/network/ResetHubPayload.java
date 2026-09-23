package me.shaweel.donsalami.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ResetHubPayload() implements CustomPacketPayload {
	public static final Type<ResetHubPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("donsalami", "reset_hub_challenge"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResetHubPayload> CODEC = StreamCodec.unit(new ResetHubPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}