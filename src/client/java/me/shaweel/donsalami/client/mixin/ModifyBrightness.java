package me.shaweel.donsalami.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.OptionInstance.CaptionBasedToString;
import net.minecraft.client.OptionInstance.TooltipSupplier;
import net.minecraft.client.OptionInstance.ValueSet;
import net.minecraft.client.OptionInstance.ValueUpdateListener;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

@Mixin(Options.class)
public class ModifyBrightness {
	@Redirect(
		method = "<init>",
		at = @At(
			value = "NEW",
			target = "net/minecraft/client/OptionInstance"
		)
	)
	private OptionInstance<Object> replaceGammaOption(
		String captionId,
		TooltipSupplier<Object> tooltip,
		CaptionBasedToString<Object> toString,
		ValueSet<Object> values,
		Object initialValue,
		ValueUpdateListener<? super Object> onValueUpdate
	) {
		if (captionId != "options.gamma") return new OptionInstance<Object>(captionId, tooltip, toString, values, initialValue, onValueUpdate);

		return new OptionInstance<Object>(
			captionId,
			tooltip,
			(caption, value) -> {
				return Component.translatable("options.percent_value", new Object[]{caption, (int)((double)value * (double)100.0F)});
			},
			OptionInstance.UnitDouble.INSTANCE.xmap(
				value -> (double)value * 5.0,
				value -> (double)value / 5.0
   			),
			5d, onValueUpdate
		);
	}
}
