package me.shaweel.donsalami.client.mixin.gui;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

@Mixin(SelectWorldScreen.class)
public class ModifySelectWorldScreen {
	@Shadow private Button deleteButton;
	@Shadow private Button playWorldButton;
	@Shadow private Button editButton;
	@Shadow private HeaderAndFooterLayout layout;
	@Shadow protected Screen lastScreen;

	@ModifyArg(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/Screen;<init>(Lnet/minecraft/network/chat/Component;)V"
		)
	)
	private static Component modifyTitle(Component original) {
		return Component.literal("Select Save");
	}

	@Inject(method = "createFooterButtons", at = @At("HEAD"), cancellable = true)
	private void createFooterButtons(Consumer<WorldSelectionList.WorldListEntry> joinWorld, WorldSelectionList list, CallbackInfo callbackInfo) {
		GridLayout footer = (GridLayout)this.layout.addToFooter((new GridLayout()).columnSpacing(8).rowSpacing(4));
		footer.defaultCellSetting().alignHorizontallyCenter();

		GridLayout.RowHelper rowHelper = footer.createRowHelper(2);

		this.playWorldButton = (Button)rowHelper.addChild(Button.builder(Component.literal("Play selected save"), (button) -> list.getSelectedOpt().ifPresent(joinWorld)).build());
		this.deleteButton = (Button)rowHelper.addChild(Button.builder(Component.translatable("selectWorld.delete"), (button) -> list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)).build());
		this.editButton = (Button)rowHelper.addChild(Button.builder(Component.translatable("selectWorld.edit"), (button) -> list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld)).build());
		rowHelper.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> Minecraft.getInstance().gui.setScreen(this.lastScreen)).build());

		callbackInfo.cancel();
	}
}
