package cn.com.fakeneko.config.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Manages a set of {@link ConfigCategory categories} and their serialization.
 */
public interface ConfigManager {
	/**
	 * Creates and registers a new category.
	 *
	 * @param name The category name.
	 * @param displayName The display name.
	 * @return The created category.
	 */
	ConfigCategory createCategory(@NotNull String name, @NotNull net.minecraft.network.chat.Component displayName);

	/**
	 * Registers an existing category.
	 *
	 * @param category The category.
	 */
	void registerCategory(@NotNull ConfigCategory category);

	/**
	 * Gets a registered category by name.
	 *
	 * @param name The category name.
	 * @return The category, or null if not found.
	 */
	ConfigCategory getCategory(@NotNull String name);

	/**
	 * All registered categories.
	 *
	 * @return The categories.
	 */
	@NotNull
	Collection<ConfigCategory> categories();

	/**
	 * Loads all configuration values from disk.
	 */
	void load();

	/**
	 * Saves all configuration values to disk.
	 */
	void save();

	/**
	 * Resets all configs to their default values.
	 */
	default void resetAll() {
		for (ConfigCategory category : this.categories()) {
			for (Config<?> config : category.configs()) {
				config.reset();
			}
		}
	}
}
