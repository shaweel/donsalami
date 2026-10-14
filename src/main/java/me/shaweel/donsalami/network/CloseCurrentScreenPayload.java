package me.shaweel.donsalami.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CloseCurrentScreenPayload() implements CustomPacketPayload {
	public static final Type<CloseCurrentScreenPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("donsalami", "close_current_screen"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CloseCurrentScreenPayload> CODEC = StreamCodec.unit(new CloseCurrentScreenPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}