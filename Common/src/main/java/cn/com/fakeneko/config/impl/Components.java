package cn.com.fakeneko.config.impl;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Minecraft 1.18 lacks the static factory methods on {@code Component}
 * (they were added in 1.19). These helpers bridge that gap.
 */
public final class Components {
	private Components() {
	}

	public static MutableComponent literal(String text) {
		return new TextComponent(text);
	}

	public static MutableComponent translatable(String key) {
		return new TranslatableComponent(key);
	}

	public static MutableComponent translatable(String key, Object... args) {
		return new TranslatableComponent(key, args);
	}

	public static MutableComponent empty() {
		return TextComponent.EMPTY.copy();
	}

	public static MutableComponent nullToEmpty(String text) {
		return text == null ? TextComponent.EMPTY.copy() : new TextComponent(text);
	}
}
