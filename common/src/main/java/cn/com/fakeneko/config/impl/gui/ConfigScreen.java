package cn.com.fakeneko.config.impl.gui;

import cn.com.fakeneko.config.api.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
	private static final Component TITLE = Component.translatable("config.fakeneko_config.title");
	private static final Component SEARCH = Component.translatable("config.fakeneko_config.search");
	private static final Component SAVE = Component.translatable("config.fakeneko_config.save");
	private static final Component RESET = Component.translatable("config.fakeneko_config.reset");

	private final Screen lastScreen;
	private final ConfigManager manager;
	private ConfigList configList;
	private EditBox searchBox;

	public ConfigScreen(Screen lastScreen, @NotNull ConfigManager manager) {
		super(TITLE);
		this.lastScreen = lastScreen;
		this.manager = manager;
	}

	public ConfigManager manager() {
		return this.manager;
	}

	@Override
	protected void init() {
		this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, SEARCH);
		this.searchBox.setResponder(value -> this.configList.setFilter(value));
		this.addRenderableWidget(this.searchBox);

		this.configList = new ConfigList(this, this.minecraft, this.width, this.height - 90, 50, 24);
		this.addRenderableWidget(this.configList);

		this.addRenderableWidget(Button.builder(RESET, button -> this.onReset())
			.bounds(this.width / 2 - 155, this.height - 30, 150, 20)
			.build());
		this.addRenderableWidget(Button.builder(SAVE, button -> this.onSave())
			.bounds(this.width / 2 + 5, this.height - 30, 150, 20)
			.build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.text(Minecraft.getInstance().font, this.title, this.width / 2 - this.font.width(this.title) / 2, 8, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(this.lastScreen);
	}

	private void onSave() {
		this.manager.save();
		this.minecraft.gui.setScreen(this.lastScreen);
	}

	private void onReset() {
		this.manager.resetAll();
		this.configList.refreshEntries();
	}
}
