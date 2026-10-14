package me.shaweel.donsalami.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ShowGenericMessageScreenPayload(Component message) implements CustomPacketPayload {
	public static final Type<ShowGenericMessageScreenPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("donsalami", "show_generic_message_screen"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShowGenericMessageScreenPayload> CODEC = StreamCodec.composite(
		ComponentSerialization.STREAM_CODEC,
		ShowGenericMessageScreenPayload::message,
		ShowGenericMessageScreenPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}