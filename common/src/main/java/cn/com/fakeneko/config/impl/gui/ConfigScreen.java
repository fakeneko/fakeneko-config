package cn.com.fakeneko.config.impl.gui;

import cn.com.fakeneko.config.api.Config;
import cn.com.fakeneko.config.api.ConfigCategory;
import cn.com.fakeneko.config.api.ConfigManager;
import cn.com.fakeneko.config.impl.types.BooleanConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
	private static final Component SEARCH = Component.translatable("config.fakeneko_config.search");
	private static final Component CANCEL = Component.translatable("config.fakeneko_config.cancel");
	private static final Component DONE = Component.translatable("config.fakeneko_config.done");

	private final Screen lastScreen;
	private final ConfigManager manager;
	private final java.util.Map<Config<?>, Object> initialValues = new java.util.IdentityHashMap<>();
	private final java.util.Map<Config<?>, Object> pendingValues = new java.util.IdentityHashMap<>();
	private ConfigList configList;
	private EditBox searchBox;
	private Button cancelButton;
	private Button doneButton;
	private double scrollAmount;

	public ConfigScreen(Screen lastScreen, @NotNull ConfigManager manager) {
		super(manager.displayName());
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
		this.configList.setScrollAmount(this.scrollAmount);
		this.addRenderableWidget(this.configList);

		this.cancelButton = Button.builder(CANCEL, button -> this.onCancel())
			.bounds(this.width / 2 - 155, this.height - 30, 150, 20)
			.build();
		this.doneButton = Button.builder(DONE, button -> this.onDone())
			.bounds(this.width / 2 + 5, this.height - 30, 150, 20)
			.build();
		this.addRenderableWidget(this.cancelButton);
		this.addRenderableWidget(this.doneButton);

		for (ConfigCategory category : this.manager.categories()) {
			for (Config<?> config : category.configs()) {
				this.captureInitialValue(config);
				if (config instanceof BooleanConfig booleanConfig && booleanConfig.hotkey() != null) {
					this.captureInitialValue(booleanConfig.hotkey());
				}
			}
		}
		this.updateDoneButton();
	}

	private void captureInitialValue(Config<?> config) {
		this.initialValues.put(config, config.get());
	}

	@SuppressWarnings("unchecked")
	public <T> T getEffectiveValue(Config<T> config) {
		Object pending = this.pendingValues.get(config);
		if (pending != null) {
			return (T) pending;
		}
		return config.get();
	}

	public boolean hasPendingValue(Config<?> config) {
		return this.pendingValues.containsKey(config);
	}

	public void setPendingValue(Config<?> config, Object value) {
		Object initial = this.initialValues.get(config);
		if (initial == null) {
			this.pendingValues.put(config, value);
		} else if (this.areValuesEqual(initial, value)) {
			this.pendingValues.remove(config);
		} else {
			this.pendingValues.put(config, value);
		}
		this.configList.refreshEntry(config);
		this.updateDoneButton();
	}

	private boolean areValuesEqual(Object a, Object b) {
		if (a instanceof java.util.List<?> listA && b instanceof java.util.List<?> listB) {
			return listA.equals(listB);
		}
		return java.util.Objects.equals(a, b);
	}

	private void applyPendingValues() {
		for (java.util.Map.Entry<Config<?>, Object> entry : this.pendingValues.entrySet()) {
			this.applyPendingValue(entry.getKey(), entry.getValue());
		}
		this.pendingValues.clear();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void applyPendingValue(Config<?> config, Object value) {
		((Config) config).set(value);
	}

	@Override
	public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
		this.setFocused(null);
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.text(Minecraft.getInstance().font, this.title, this.width / 2 - this.font.width(this.title) / 2, 8, -1);
	}

	@Override
	public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
		if (event.key() == com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE) {
			this.onCancel();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		// No-op: Minecraft.setScreen() calls onClose() when opening child screens.
		// Actual close/cancel is handled by keyPressed(ESC) or the cancel button.
	}

	public void setScrollAmount(double amount) {
		this.scrollAmount = amount;
	}

	private void onDone() {
		this.applyPendingValues();
		this.pendingValues.clear();
		this.initialValues.clear();
		this.manager.save();
		this.minecraft.gui.setScreen(this.lastScreen);
	}

	private void onCancel() {
		this.pendingValues.clear();
		this.initialValues.clear();
		this.minecraft.gui.setScreen(this.lastScreen);
	}

	public boolean isModifiedFromInitial(Config<?> config) {
		Object initial = this.initialValues.get(config);
		if (initial == null) {
			return config.isModified();
		}
		Object current = this.getEffectiveValue(config);
		return !this.areValuesEqual(initial, current);
	}

	public boolean isModifiedFromInitial() {
		for (ConfigCategory category : this.manager.categories()) {
			for (Config<?> config : category.configs()) {
				if (this.isModifiedFromInitial(config)) {
					return true;
				}
				if (config instanceof BooleanConfig booleanConfig && booleanConfig.hotkey() != null) {
					if (this.isModifiedFromInitial(booleanConfig.hotkey())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void updateDoneButton() {
		this.doneButton.active = this.isModifiedFromInitial();
	}
}
