package me.shaweel.donsalami.client.gui;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.shaweel.donsalami.DonSalami;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FileUtil;

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

	private static final Path savesDirectory = Minecraft.getInstance().gameDirectory.toPath().resolve("saves");

	private final ExecutorService worldExecutor = Executors.newSingleThreadExecutor();

	private EditBox name;
	private StringWidget saveDirectory;
	private String worldName;
	private String directoryName;

	private void extractWorld() {
		Path destination = savesDirectory.resolve(this.directoryName);

		try (InputStream inputStream = DonSalami.class.getResourceAsStream("/world.zip")) {
			ZipInputStream zip = new ZipInputStream(inputStream);
			Files.createDirectories(destination);

			for (ZipEntry entry; (entry = zip.getNextEntry()) != null;) {
				Path file = destination.resolve(entry.getName());
				
				if (entry.isDirectory()) Files.createDirectories(file);
				else {
					Files.createDirectories(file.getParent());
					Files.copy(zip, file, StandardCopyOption.REPLACE_EXISTING);
				}
			}

			Path levelDat = destination.resolve("level.dat");

			CompoundTag nbt = NbtIo.readCompressed(levelDat, NbtAccounter.unlimitedHeap());
			Optional<CompoundTag> optionalData = nbt.getCompound("Data");

			optionalData.ifPresent(data -> {
				data.putString("LevelName", this.worldName);

				try {
					NbtIo.writeCompressed(nbt, levelDat);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openWorld() {
		Minecraft minecraft = this.minecraft;
		minecraft.setScreenAndShow(new WelcomeScreen());

		worldExecutor.submit(() -> {
			extractWorld();

			minecraft.execute(() -> {
				WorldOpenFlows worldOpenFlows = new WorldOpenFlows(minecraft, minecraft.getLevelSource());
				worldOpenFlows.openWorld(this.directoryName, () -> {});
			});
		});
	}

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

	private int saveDirectoryTextX() {
		return textBoxX();
	}

	private int saveDirectoryTextY() {
		return textBoxY() + TEXTBOX_HEIGHT + LABEL_TEXT_Y_OFFSET;
	}

	public NewGameScreen() {
		super(Component.literal("New Game"));
	}

	private Component getSavedDirectoryComponent() {
		return Component.literal("Will be saved as: ")
			.withColor(TextColor.GRAY)
			.append(Component.literal(this.directoryName)
			.withStyle(ChatFormatting.ITALIC)
			.withColor(TextColor.GRAY));
	}

	@Override 
	protected void init() {
		this.name = this.addRenderableWidget(new EditBox(this.font, textBoxX(), textBoxY(), TEXTBOX_WIDTH, TEXTBOX_HEIGHT, Component.empty()));
		this.name.setResponder(text -> {
			this.worldName = text;

			try {
				this.directoryName = FileUtil.findAvailableName(savesDirectory, text, "");
				this.saveDirectory.setMessage(this.getSavedDirectoryComponent());

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});

		try {
			this.directoryName = FileUtil.findAvailableName(savesDirectory, "New Salami", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.saveDirectory = this.addRenderableWidget(new StringWidget(
			saveDirectoryTextX(),
			saveDirectoryTextY(),
			this.font.width(this.getSavedDirectoryComponent()),
			this.font.lineHeight,
			this.getSavedDirectoryComponent(),
			this.font
		));

		this.name.setValue("New Salami");

		this.addRenderableWidget(
			new Button.Builder(Component.literal("Create Save"), button -> this.openWorld())
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
