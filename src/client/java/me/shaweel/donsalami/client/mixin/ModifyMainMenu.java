package me.shaweel.donsalami.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.donsalami.client.NewGameScreen;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

@Mixin(TitleScreen.class)
public class ModifyMainMenu {
	@Shadow private static Component COPYRIGHT_TEXT;
	@Shadow private SplashRenderer splash;
	@Shadow private int createNormalMenuOptions(int topPos, final int spacing) {return 0;};

	TitleScreen titleScreen = (TitleScreen)(Object)this;
	ScreenAccessor screen = (ScreenAccessor)titleScreen;
	
	private static void newGame() {
		Minecraft.getInstance().gui.setScreen(new NewGameScreen());
		//Path savesDirectotry = Minecraft.getInstance().gameDirectory.toPath().resolve("saves");
	}

	@Inject(at = @At("HEAD"), method = "createNormalMenuOptions", cancellable = true)
	private void changeButtons(int topPos, int spacing, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
		Button newGameButton = screen.invokeAddRenderableWidget(
			Button.builder(Component.literal("New Game"), var1 -> newGame())
				.bounds(titleScreen.width / 2 - 100, topPos, 200, 20)
				.build()
		);

		if (SharedConstants.IS_RUNNING_IN_IDE) {
			screen.invokeAddRenderableWidget(
				Button.builder(Component.literal("TW"), var1 -> CreateWorldScreen.testWorld(Minecraft.getInstance(), () -> Minecraft.getInstance().gui.setScreen(titleScreen)))
					.bounds(newGameButton.getX() + newGameButton.getWidth() + 2, topPos, 20, 20)
					.build()
			);
		}

		screen.invokeAddRenderableWidget(
			Button.builder(Component.literal("Load Game"), var1 -> Minecraft.getInstance().gui.setScreen(new SelectWorldScreen(titleScreen)))
				.bounds(titleScreen.width / 2 - 100, topPos = topPos + spacing, 200, 20)
				.build()
		);

		callbackInfoReturnable.setReturnValue(topPos);
	}

	@Inject(at = @At("HEAD"), method = "init", cancellable = true)
	private void init(CallbackInfo callbackInfo) {
		if (splash == null) {
			splash = Minecraft.getInstance().gui.splashManager().getSplash();
		}

		int copyrightWidth = titleScreen.getFont().width(COPYRIGHT_TEXT);
		int copyrightX = titleScreen.width - copyrightWidth - 2;
		int topPos = titleScreen.height / 4 + 72;
			
		topPos = createNormalMenuOptions(topPos, 24);
		
		Button.Builder var10001 = Button.builder(
			Component.translatable("menu.options"), var1x -> Minecraft.getInstance().gui.setScreen(new OptionsScreen(titleScreen, Minecraft.getInstance().options, false))
		);
		int var10002 = titleScreen.width / 2 - 100;
		topPos += 36;
		screen.invokeAddRenderableWidget(var10001.bounds(var10002, topPos, 98, 20).build());
		screen.invokeAddRenderableWidget(
			Button.builder(Component.translatable("menu.quit"), var1x -> Minecraft.getInstance().stop()).bounds(titleScreen.width / 2 + 2, topPos, 98, 20).build()
		);
		screen.invokeAddRenderableWidget(
			new PlainTextButton(
				copyrightX, titleScreen.height + 1, copyrightWidth, 10, COPYRIGHT_TEXT, var1x -> Minecraft.getInstance().gui.setScreen(new CreditsAndAttributionScreen(titleScreen)), titleScreen.getFont()
			)
		);

		callbackInfo.cancel();
	}
}
