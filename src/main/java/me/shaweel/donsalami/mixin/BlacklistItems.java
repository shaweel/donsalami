package me.shaweel.donsalami.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@Mixin(Inventory.class)
public class BlacklistItems {
	private static Set<Item> BLACKLISTED_ITEMS = Set.of(
		Items.PISTON,
		Items.STICKY_PISTON
	);

	private static int messageCooldown = 0;

	@Inject(at = @At("HEAD"), method = "tick")
	private void dontAddBlacklisted(CallbackInfo callbackInfo) {
		messageCooldown -= 1;

		Inventory inventory = (Inventory)(Object)this;

		List<String> deleted = new ArrayList<>();

		for (Item item : BLACKLISTED_ITEMS) {
			if (!inventory.contains(stack -> stack.is(item))) continue;

			deleted.add(item.getName(item.getDefaultInstance()).getString());

			inventory.clearOrCountMatchingItems(
				stack -> stack.is(item),
				Integer.MAX_VALUE,
				inventory
			);
		}

		if (deleted.isEmpty() || messageCooldown > 0) return;

		messageCooldown = 20;

		inventory.player.sendSystemMessage(Component.literal("We have deleted the following blacklisted item(s) from your inventory: ").withColor(TextColor.RED)
		.append(Component.literal(String.join(", ", deleted)).withColor(TextColor.WHITE)));
	}
}
