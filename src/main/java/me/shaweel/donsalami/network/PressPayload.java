package me.shaweel.donsalami.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PressPayload() implements CustomPacketPayload {
	public static final Type<PressPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("donsalami", "press"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PressPayload> CODEC = StreamCodec.unit(new PressPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}