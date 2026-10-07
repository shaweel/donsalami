package me.shaweel.donsalami.client.gui;

import java.io.IOException;
import java.io.InputStream;

import com.mojang.blaze3d.platform.NativeImage;

import me.shaweel.donsalami.DonSalami;
import me.shaweel.donsalami.client.KeyMappings;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;

public class WelcomeScreen extends Screen {
	private static final Identifier HOTBAR_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar");
	private static final Identifier CROSSHAIR_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair");
	private static final Identifier HEART_CONTAINER_TEXTURE = Identifier.withDefaultNamespace("hud/heart/container");
	private static final Identifier HEART_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/heart/full");
	private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("social_interactions/background");
	
	private static final int FRAME_WIDTH = 384;
	private static final int FRAME_HEIGHT = 216;
	private static int LOADING_TEXT_PADDING = 4;
	private static int FRAME_SIZE = 8;
	private static int TITLE_PADDING = 6;
	private static int DESCRIPTION_PADDING = 4;
	private static int IMAGE_PADDING = 4;
	private static int PAGE_NUMBER_TEXT_PADDING = 3;
	private static int PAGE_BUTTON_PADDING = 3;
	private static int PAGE_BUTTON_WIDTH = 12;
	private static int PAGE_BUTTON_HEIGHT = 17;
	private static int CLOSE_BUTTON_WIDTH = 50;
	private static int CLOSE_BUTTON_HEIGHT = 20;
	private static int TITLE_SCALE = 2;
	private static int TEXT_COLOR = 0xFFFFFFFF;

	private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(
		Identifier.withDefaultNamespace("recipe_book/page_forward"),
		Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted")
	);
	private static final WidgetSprites PAGE_BACK_SPRITES = new WidgetSprites(
		Identifier.withDefaultNamespace("recipe_book/page_backward"), 
		Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted")
	);

	private static int PAGE_AMOUNT = 5;
	private int currentPage = 0;
	private static boolean shouldClose = false;
	private long joinedWorldAt;

	public WelcomeScreen() {
		super(Component.literal("Welcome Screen"));
	}
	
	@Override
	public void onClose() {
		if (Minecraft.getInstance().level == null) {
			return;
		}

		shouldClose = true;
		this.minecraft.gui.setScreen((Screen)null);
		shouldClose = false;
	}

	@Override
	public boolean isPauseScreen() {
		if (Minecraft.getInstance().level == null) {
			joinedWorldAt = -1;
			return false;
		}

		if (joinedWorldAt == -1) {
			joinedWorldAt = System.currentTimeMillis();
		}

		if (System.currentTimeMillis() - this.joinedWorldAt > 5000) {
			return true;
		}

		return false;
	}

	private void extractFakeGame(GuiGraphicsExtractor graphics) {
		graphics.blit(
			RenderPipelines.GUI_TEXTURED,
			DonSalami.id("textures/new_game_background.png"),
			0, 0,
			0, 0,
			this.width, this.height,
			1920, 1080,
			1920, 1080
		);
		this.extractFakeCrosshair(graphics);
		this.extractFakeHotbarAndDecorations(graphics);
		SplitsHud.extractRenderState(graphics, DeltaTracker.ZERO);

		graphics.nextStratum();
		this.extractBlurredBackground(graphics);
		this.extractMenuBackground(graphics);
	}

	private void extractFakeCrosshair(GuiGraphicsExtractor graphics) {
		graphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_TEXTURE, (graphics.guiWidth() - 15) / 2, (graphics.guiHeight() - 15) / 2, 15, 15);
	}

	private void extractFakeHotbarAndDecorations(GuiGraphicsExtractor graphics) {
		int hotbarX = this.width / 2 - 91;
		int hotbarY = this.height - 22;
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, hotbarX, hotbarY, 182, 22);

		int heartY = this.height - 33;
		for (int i = 0; i < 10; i++) {
			int x = hotbarX + i * 8;
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_CONTAINER_TEXTURE, x, heartY, 9, 9);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_FULL_TEXTURE, x, heartY, 9, 9);
		}
	}

	@Override 
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (Minecraft.getInstance().level != null) {
			this.extractBlurredBackground(graphics);
			this.extractMenuBackground(graphics);
			return;
		}

		this.extractFakeGame(graphics);

		graphics.text(font, "Your new save is loading in the background...", LOADING_TEXT_PADDING, LOADING_TEXT_PADDING, TEXT_COLOR);
		graphics.text(font, "Because of this you might experience lag on poorer computers.", LOADING_TEXT_PADDING, LOADING_TEXT_PADDING + this.font.lineHeight + 1, TEXT_COLOR);
	}

	private int frameX0() {
		return (this.width - FRAME_WIDTH) / 2;
	}

	private int frameX1() {
		return frameX0() + FRAME_WIDTH;
	}

	private int frameY0() {
		return (this.height - FRAME_HEIGHT) / 2;
	}

	private int frameY1() {
		return frameY0() + FRAME_HEIGHT;
	}

	private int titleX() {
		return this.width / 2;
	}

	private int titleY() {
		return this.frameY0() + TITLE_PADDING + FRAME_SIZE;
	}

	private int descriptionX() {
		return this.frameX0() + DESCRIPTION_PADDING + FRAME_SIZE;
	}

	private int descriptionY() {
		return this.titleY() + this.font.lineHeight * TITLE_SCALE + DESCRIPTION_PADDING;
	}

	private int textWidth() {
		return this.frameX1() - this.frameX0() - DESCRIPTION_PADDING * 2 - FRAME_SIZE * 2;
	}

	private int pageNumberX() {
		return this.width / 2;
	}

	private int pageNumberY() {
		return this.frameY1() - FRAME_SIZE - PAGE_NUMBER_TEXT_PADDING - this.font.lineHeight;
	}

	private int pageBackX() {
		return frameX0() + FRAME_SIZE + PAGE_BUTTON_PADDING;
	}

	private int pageBackY() {
		return frameY1() - FRAME_SIZE - PAGE_BUTTON_PADDING - PAGE_BUTTON_HEIGHT;
	}

	private int pageForwardX() {
		return frameX1() - FRAME_SIZE - PAGE_BUTTON_PADDING - PAGE_BUTTON_WIDTH;
	}

	private int pageForwardY() {
		return frameY1() - FRAME_SIZE - PAGE_BUTTON_PADDING - PAGE_BUTTON_HEIGHT;
	}

	private int closeButtonX() {	
		return frameX1() - FRAME_SIZE - PAGE_BUTTON_PADDING - CLOSE_BUTTON_WIDTH;
	}

	private int closeButtonY() {
		return frameY1() - FRAME_SIZE - PAGE_BUTTON_PADDING - CLOSE_BUTTON_HEIGHT;
	}

	private void extractMainFrame(GuiGraphicsExtractor graphics) {
		int x0 = this.frameX0();
		int y0 = this.frameY0();
		int width = this.frameX1() - this.frameX0();
		int height = this.frameY1() - this.frameY0();

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, x0, y0, width, height);
	}

	private void extractTitle(GuiGraphicsExtractor graphics) {
		String[] titles = new String[]{"Welcome", "QoL", "Splits", "Healing", "Good Luck"};
	
		graphics.pose().pushMatrix();
		graphics.pose().scale(TITLE_SCALE);

		graphics.centeredText(font, titles[this.currentPage], (int) (this.titleX() / TITLE_SCALE), (int) (this.titleY() / TITLE_SCALE), TEXT_COLOR);

		graphics.pose().popMatrix();
	}

	private int[] getImageSize(Identifier image) {
		try (InputStream inputStream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(image).open()) {
			NativeImage nativeImage = NativeImage.read(inputStream);

			int width = nativeImage.getWidth();
			int height = nativeImage.getHeight();

			nativeImage.close();

			return new int[]{width, height};
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load image: " + image, e);
		}
	}

	private void extractImage(GuiGraphicsExtractor graphics, int descriptionEndY) {
		Identifier[] images = new Identifier[]{
			Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "textures/item/salami.png"),
			Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "textures/welcome_screen/buried_treasure.png"),
			Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "textures/welcome_screen/splits.png"),
			Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "textures/welcome_screen/no_hunger.png"),
			Identifier.fromNamespaceAndPath(DonSalami.MOD_ID, "textures/item/salami.png")
		};

		int verticalSpaceForImage = Math.min(
			frameY1() - FRAME_SIZE - PAGE_BUTTON_HEIGHT - descriptionEndY - IMAGE_PADDING * 2,
			(descriptionEndY - (frameY0() + FRAME_SIZE + DESCRIPTION_PADDING)) * 3
		);
		int horizontalSpaceForImage = frameX1() - frameX0() - FRAME_SIZE * 2 - IMAGE_PADDING * 2;

		int[] imageSize = getImageSize(images[this.currentPage]);
		int imageWidth = imageSize[0];
		int imageHeight = imageSize[1];

		float scale = Math.min((float) horizontalSpaceForImage / imageWidth, (float) verticalSpaceForImage / imageHeight);

		int drawWidth = Math.round(imageWidth * scale);
		int drawHeight = Math.round(imageHeight * scale);

		int imageX = frameX0() + FRAME_SIZE + IMAGE_PADDING;
		int imageY = descriptionEndY + IMAGE_PADDING;

		graphics.blit(
			RenderPipelines.GUI_TEXTURED,
			images[this.currentPage],
			imageX, imageY,
			0, 0,
			drawWidth, drawHeight,
			imageWidth, imageHeight,
			imageWidth, imageHeight
		);
	}

	private void extractPageNumber(GuiGraphicsExtractor graphics) {
		String string = String.format("Page %s of %s", this.currentPage + 1, PAGE_AMOUNT);
		graphics.centeredText(font, string, this.pageNumberX(), this.pageNumberY(), TEXT_COLOR);
	}

	private void initPageControlButtons() {
		if (this.currentPage != 0) {
			this.addRenderableWidget(new ImageButton(
				pageBackX(), pageBackY(), PAGE_BUTTON_WIDTH, PAGE_BUTTON_HEIGHT, PAGE_BACK_SPRITES, button -> {
					this.currentPage -= 1;
					this.clearWidgets();
					this.init();
			}));
		}

		if (this.currentPage != PAGE_AMOUNT - 1) {
			this.addRenderableWidget(new ImageButton(
				pageForwardX(), pageForwardY(), PAGE_BUTTON_WIDTH, PAGE_BUTTON_HEIGHT, PAGE_FORWARD_SPRITES, button -> {
					this.currentPage += 1;
					this.clearWidgets();
					this.init();
			}));
		} else {
			this.addRenderableWidget(
				new Button.Builder(Component.literal("Close"), button -> this.onClose())
				.bounds(closeButtonX(), closeButtonY(), CLOSE_BUTTON_WIDTH, CLOSE_BUTTON_HEIGHT)
				.build());
		}
	}

	private int extractDescription(GuiGraphicsExtractor graphics) {
		String[] description = new String[]{
			String.format(
				"Welcome to Don Salami. Please don't skip reading this as it will include a lot useful information. " + 
				"If you ever want to come back to this screen, press %s", KeyMappingHelper.getBoundKeyOf(KeyMappings.reopenWelcome).getDisplayName().getString()),
			"We've made some quality of life changes to make your experience better. These are: " +
			"Capping the brightness at 500% and slightly hinting buried treasure's location to circumvent mapless being impossible.",
			String.format("At the top left of your screen you will find a HUD showing your current split. Similarly to speedrunning, splits are significant parts of the run. " +
			"Here, think of them like checkpoints, every time you enter a new split, your spawnpoint, " + 
			"inventory and the map will be saved to as when you entered the split. " +
			"This is so that whenever the split is reset, either by death or willingly by pressing %s, " + 
			"you will be left in as similar of a state as when you entered it as possible.", 
			KeyMappingHelper.getBoundKeyOf(KeyMappings.resetSplit).getDisplayName().getString()),
			"We've revamped healing to better fit this map: Hunger has been removed and healing is now exclusive to dedicated healing items.",
			"With all that said, good luck on beating our mod. We hope you enjoy it."
		};

		graphics.textWithWordWrap(font, FormattedText.of(description[this.currentPage]), descriptionX(), descriptionY(), textWidth(), TEXT_COLOR);

		return font.split(FormattedText.of(description[this.currentPage]), textWidth()).size();
	}

	@Override 
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		extractMainFrame(graphics);
		extractTitle(graphics);
		int descriptionEndY = extractDescription(graphics) * (font.lineHeight + 1) + descriptionY();
		extractImage(graphics, descriptionEndY);
		extractPageNumber(graphics);

		super.extractRenderState(graphics, mouseX, mouseY, delta);
	}

	public static boolean shouldClose() {
		return shouldClose;
	}

	@Override
	public void init() {
		initPageControlButtons();
	}
}