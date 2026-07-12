package cn.com.fakeneko.config;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class FakenekoConfig {
	public static final String MOD_ID = "fakeneko_config";

	private FakenekoConfig() {

	}

	@NotNull
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
