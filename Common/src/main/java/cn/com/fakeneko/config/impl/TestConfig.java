package cn.com.fakeneko.config.impl;

import com.mojang.blaze3d.platform.InputConstants;
import cn.com.fakeneko.config.FakenekoConfig;
import cn.com.fakeneko.config.api.ConfigCategory;
import cn.com.fakeneko.config.api.ConfigManager;
import cn.com.fakeneko.config.impl.types.*;
import cn.com.fakeneko.config.impl.keybind.InputKeys;
import net.minecraft.network.chat.Component;
import cn.com.fakeneko.config.impl.Components;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger("TestConfig");
	public static final ConfigManager MANAGER = new ConfigManagerImpl(
		FakenekoConfig.MOD_ID,
		Components.translatable("config.fakeneko_config.title")
	);

	public static final ConfigCategory GENERAL = MANAGER.createCategory("general", Components.translatable("config.fakeneko_config.category.general"));
	public static final ConfigCategory ADVANCED = MANAGER.createCategory("advanced", Components.translatable("config.fakeneko_config.category.advanced"));

	public static final BooleanConfig ENABLED = new BooleanConfig("enabled", Components.translatable("config.fakeneko_config.enabled"), GENERAL, true)
		.withHotkey(Components.translatable("config.fakeneko_config.enabled_hotkey"), FakenekoConfig.id("enabled"), InputKeys.of(InputConstants.KEY_H));
	public static final IntegerConfig MAX_COUNT = new IntegerConfig("max_count", Components.translatable("config.fakeneko_config.max_count"), GENERAL, 5, 1, 20);
	public static final DoubleConfig SPEED = new DoubleConfig("speed", Components.translatable("config.fakeneko_config.speed"), GENERAL, 1.0, 0.1, 5.0);
	public static final StringConfig MODE = new StringConfig("mode", Components.translatable("config.fakeneko_config.mode"), GENERAL, "default");
	public static final StringConfig PREFIX = new StringConfig("prefix", Components.translatable("config.fakeneko_config.prefix"), GENERAL, "[Test]");
	public static final StringListConfig BLACKLIST = new StringListConfig("blacklist", Components.translatable("config.fakeneko_config.blacklist"), GENERAL, java.util.List.of("example:block"));
	public static final EnumConfig<RenderMode> RENDER_MODE = new EnumConfig<>(
		"render_mode",
		Components.translatable("config.fakeneko_config.render_mode"),
		GENERAL,
		RenderMode.FANCY,
		EnumWidget.DROPDOWN,
		e -> Components.translatable("config.fakeneko_config.render_mode." + e.name().toLowerCase())
	);
	public static final EnumConfig<TooltipPosition> TOOLTIP_POSITION = new EnumConfig<>(
		"tooltip_position",
		Components.translatable("config.fakeneko_config.tooltip_position"),
		GENERAL,
		TooltipPosition.TOP_RIGHT,
		EnumWidget.CYCLIC,
		e -> Components.translatable("config.fakeneko_config.tooltip_position." + e.name().toLowerCase())
	);
	public static final HotkeyConfig TOGGLE_HOTKEY = new HotkeyConfig(
		"toggle_hotkey",
		Components.translatable("config.fakeneko_config.toggle_hotkey"),
		ADVANCED,
		InputKeys.of(InputConstants.KEY_K),
		FakenekoConfig.id("toggle"),
		keybind -> LOGGER.info("Hotkey pressed: {}", keybind.name().getString())
	);

	public enum RenderMode {
		FAST,
		FANCY,
		FABULOUS,
		MINIMAL,
		BALANCED,
		CINEMATIC,
		REALISTIC,
		CUSTOM
	}

	public enum TooltipPosition {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	static {
		ENABLED.withDescription(Components.translatable("config.fakeneko_config.enabled.desc"));
		MAX_COUNT.withDescription(Components.translatable("config.fakeneko_config.max_count.desc"));
		SPEED.withDescription(Components.translatable("config.fakeneko_config.speed.desc"));
		MANAGER.load();
	}

	private TestConfig() {

	}

	public static void init() {
		LOGGER.info("TestConfig initialized");
	}
}
