package cn.com.fakeneko.config.impl.gui;

import cn.com.fakeneko.config.impl.types.EnumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class EnumDropdownScreen<E extends Enum<E>> extends Screen {
	private static final Component TITLE = Component.translatable("config.fakeneko_config.enum.title");
	private static final Component CANCEL = Component.translatable("config.fakeneko_config.cancel");

	private final ConfigScreen lastScreen;
	private final EnumConfig<E> config;
	private final Consumer<E> onSelect;

	public EnumDropdownScreen(ConfigScreen lastScreen, EnumConfig<E> config, Consumer<E> onSelect) {
		super(TITLE);
		this.lastScreen = lastScreen;
		this.config = config;
		this.onSelect = onSelect;
	}

	@Override
	protected void init() {
		E[] values = this.config.values();
		int buttonWidth = 150;
		int buttonHeight = 20;
		int gap = 5;
		int totalHeight = values.length * buttonHeight + (values.length - 1) * gap;
		int startY = this.height / 2 - totalHeight / 2;

		for (int i = 0; i < values.length; i++) {
			E value = values[i];
			this.addRenderableWidget(Button.builder(this.config.displayValue(value), button -> {
				this.onSelect.accept(value);
				this.minecraft.gui.setScreen(this.lastScreen);
			}).bounds(this.width / 2 - buttonWidth / 2, startY + i * (buttonHeight + gap), buttonWidth, buttonHeight).build());
		}

		this.addRenderableWidget(Button.builder(CANCEL, button -> this.minecraft.gui.setScreen(this.lastScreen))
			.bounds(this.width / 2 - buttonWidth / 2, startY + values.length * (buttonHeight + gap) + 10, buttonWidth, buttonHeight).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.text(Minecraft.getInstance().font, this.title, this.width / 2 - this.font.width(this.title) / 2, 20, -1);
		for (GuiEventListener child : this.children()) {
			if (child instanceof net.minecraft.client.gui.components.Renderable renderable) {
				renderable.extractRenderState(graphics, mouseX, mouseY, a);
			}
		}
	}
}
