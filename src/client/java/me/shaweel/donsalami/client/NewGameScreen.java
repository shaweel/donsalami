package me.shaweel.donsalami.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class NewGameScreen extends Screen {
	private static final int TITLE_Y_PADDING = 8;
	private static final int FONT_COLOR = 0xFFFFFFFF;
	private static final int TEXTBOX_HEIGHT = 22;
	private static final int TEXTBOX_WIDTH = 208;
	private static final int TEXTBOX_OFFSET = 18;

	private static final int LABEL_TEXT_Y_OFFSET = 4;

	private static final int BOTTOM_BUTTON_PADDING = 6;
	private static final int BOTTOM_BUTTON_SPACING = 4;
	private static final int BOTTOM_BUTTON_HEIGHT = 20;
	private static final int BOTTOM_BUTTON_WIDTH = 150;

	private EditBox name;

	private static void openWorld() {}

	private int createSaveX() {
		return this.width / 2 - BOTTOM_BUTTON_WIDTH - BOTTOM_BUTTON_SPACING;
	}

	private int cancelX() {
		return this.width / 2 + BOTTOM_BUTTON_SPACING;
	}

	private int bottomButtonY() {
		return this.height - BOTTOM_BUTTON_HEIGHT - BOTTOM_BUTTON_PADDING;
	}

	private int textBoxX() {
		return this.width / 2 - TEXTBOX_WIDTH / 2;
	}

	private int textBoxY() {
		return this.height / 2 - TEXTBOX_HEIGHT / 2 - TEXTBOX_OFFSET;
	}

	private int labelTextXPosition() {
		return textBoxX();
	}

	private int labelTextYPosition() {
		return textBoxY() - this.font.lineHeight - LABEL_TEXT_Y_OFFSET;
	}

	public NewGameScreen() {
		super(Component.literal("New Game"));
	}

	@Override 
	protected void init() {
		this.name = this.addRenderableWidget(new EditBox(this.font, textBoxX(), textBoxY(), TEXTBOX_WIDTH, TEXTBOX_HEIGHT, Component.empty()));
		this.name.setValue("New Salami");

		this.addRenderableWidget(
			new Button.Builder(Component.literal("Create Save"), button -> openWorld())
			.bounds(createSaveX(), bottomButtonY(), BOTTOM_BUTTON_WIDTH, BOTTOM_BUTTON_HEIGHT)
			.build());

		this.addRenderableWidget(
			new Button.Builder(CommonComponents.GUI_CANCEL, button -> Minecraft.getInstance().gui.setScreen(null))
			.bounds(cancelX(), bottomButtonY(), BOTTOM_BUTTON_WIDTH, BOTTOM_BUTTON_HEIGHT)
			.build());
	}

	@Override
	protected void setInitialFocus() {
		if (this.name != null) {
			this.setInitialFocus(this.name);
		}
	}

	@Override 
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		graphics.centeredText(this.font, this.title, this.width / 2, TITLE_Y_PADDING, FONT_COLOR);
		graphics.text(this.font, Component.literal("Save name"), labelTextXPosition(), labelTextYPosition(), FONT_COLOR);
	}
}
