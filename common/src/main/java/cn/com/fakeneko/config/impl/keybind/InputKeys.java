package cn.com.fakeneko.config.impl.keybind;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.IntStream;

/**
 * This class represents a list of ordered input keys.
 */
public class InputKeys extends AbstractCollection<InputConstants.Key> {
	private static final Component SEPARATOR = Component.literal(" + ");
	public static final InputKeys EMPTY = new InputKeys(List.of());

	public static final Codec<InputKeys> CODEC = Codec.STRING.xmap(
		InputKeys::fromString,
		InputKeys::toString
	);

	private static final String SEPARATOR_STR = "+";

	private final List<InputConstants.Key> keys;

	public InputKeys(List<InputConstants.Key> keys) {
		this.keys = keys.stream().distinct().toList();
	}

	public InputKeys(InputConstants.Key[] keys) {
		this.keys = Arrays.stream(keys).distinct().toList();
	}

	public boolean isLastKey(InputConstants.Key key) {
		return !this.keys.isEmpty() && this.keys.getLast().equals(key);
	}

	@Override
	public @NotNull Iterator<InputConstants.Key> iterator() {
		return this.keys.iterator();
	}

	@Override
	public int size() {
		return this.keys.size();
	}

	@Override
	public boolean contains(Object o) {
		return this.keys.contains(o);
	}

	public static MutableComponent format(Collection<InputConstants.Key> keys) {
		if (keys.isEmpty()) {
			return Component.empty().append(InputConstants.UNKNOWN.getDisplayName());
		}
		return ComponentUtils.formatList(keys, SEPARATOR, InputConstants.Key::getDisplayName);
	}

	public static Component formatEditing(Collection<InputConstants.Key> keys) {
		return Component.literal("> ")
			.append(format(keys).withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
			.append(" <")
			.withStyle(ChatFormatting.YELLOW);
	}

	public static InputKeys of(int ...keys) {
		return ofKeys(keys);
	}

	public static InputKeys ofKeys(int ...keys) {
		return new InputKeys(IntStream.of(keys).mapToObj(InputConstants.Type.KEYSYM::getOrCreate).toList());
	}

	public static InputKeys ofMouse(int ...buttons) {
		return new InputKeys(IntStream.of(buttons).mapToObj(InputConstants.Type.MOUSE::getOrCreate).toList());
	}

	public static InputKeys ofAll(InputConstants.Key... keys) {
		return new InputKeys(Arrays.asList(keys));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof InputKeys other) {
			return Objects.equals(this.keys, other.keys);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.keys);
	}

	@Override
	public String toString() {
		if (this.keys.isEmpty()) {
			return InputConstants.UNKNOWN.getName();
		}
		return String.join(SEPARATOR_STR, this.keys.stream().map(InputConstants.Key::getName).toList());
	}

	public static InputKeys fromString(String value) {
		if (value == null || value.isBlank()) {
			return EMPTY;
		}
		String[] parts = value.split("\\+");
		List<InputConstants.Key> keys = new ArrayList<>(parts.length);
		for (String part : parts) {
			keys.add(InputConstants.getKey(part.trim()));
		}
		return new InputKeys(keys);
	}

	public static class Serializer implements JsonSerializer<InputKeys>, JsonDeserializer<InputKeys> {
		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {

		}

		@Override
		public InputKeys deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DataResult<InputKeys> result = CODEC.parse(JsonOps.INSTANCE, json);
			return result.getOrThrow(JsonParseException::new);
		}

		@Override
		public JsonElement serialize(InputKeys src, Type typeOfSrc, JsonSerializationContext context) {
			DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, src);
			return result.getOrThrow(IllegalArgumentException::new);
		}
	}
}
