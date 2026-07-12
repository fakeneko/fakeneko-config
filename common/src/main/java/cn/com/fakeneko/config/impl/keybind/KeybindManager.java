package cn.com.fakeneko.config.impl.keybind;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * Manager for all custom keybinds.
 */
public class KeybindManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("KeybindManager");
	private static final Multimap<Identifier, Consumer<Keybind>> consumers = HashMultimap.create();
	private static final Map<Identifier, Keybind> keybinds = new Object2ObjectLinkedOpenHashMap<>();
	private static final List<InputConstants.Key> held = new ArrayList<>();

	private KeybindManager() {

	}

	public static Keybind register(Identifier id, Keybind keybind) {
		Keybind previous = keybinds.put(id, keybind);
		if (previous != null) {
			LOGGER.warn("Overwriting keybind {}, {} -> {}", id, previous.name().getString(), keybind.name().getString());
		}
		consumers.removeAll(id).forEach(c -> c.accept(keybind));
		return keybind;
	}

	public static Keybind register(Identifier id, InputKeys keys) {
		Component name = Component.translatable("key.%s.%s".formatted(id.getNamespace(), id.getPath()));
		Keybind keybind = new SimpleKeybind(name, keys);
		return register(id, keybind);
	}

	public static Keybind register(Identifier id) {
		return register(id, InputKeys.EMPTY);
	}

	public static boolean unregister(Identifier id) {
		Keybind keybind = keybinds.remove(id);
		return keybind != null;
	}

	public static Optional<Keybind> get(Identifier id) {
		return Optional.ofNullable(keybinds.get(id));
	}

	public static void apply(Identifier id, Consumer<Keybind> consumer) {
		Keybind keybind = keybinds.get(id);
		if (keybind != null) {
			consumer.accept(keybind);
			return;
		}
		consumers.put(id, consumer);
	}

	public static void press(InputConstants.Key key) {
		if (!held.contains(key)) {
			held.add(key);
		}
		for (Keybind keybind : keybinds.values()) {
			if (keybind.keys().isLastKey(key) && areKeysHeld(keybind.keys())) {
				keybind.hold();
				keybind.click();
			}
		}
	}

	public static void release(InputConstants.Key key) {
		held.remove(key);
		for (Keybind keybind : keybinds.values()) {
			if (keybind.keys().contains(key)) {
				keybind.release();
			}
		}
	}

	private static boolean areKeysHeld(InputKeys keys) {
		for (InputConstants.Key key : keys) {
			if (!held.contains(key)) {
				return false;
			}
		}
		return true;
	}
}
