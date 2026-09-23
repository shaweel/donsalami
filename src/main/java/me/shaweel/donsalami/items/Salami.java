package me.shaweel.donsalami.items;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class Salami extends Item {
	public Salami(Properties properties) {
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
		textConsumer.accept(Component.literal("Restores full hunger and saturation but takes 10 seconds to eat").withColor(TextColor.GRAY));
	}
}
