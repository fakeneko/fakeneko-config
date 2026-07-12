package cn.com.fakeneko.config.neoforge;

import cn.com.fakeneko.config.impl.TestConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("fakeneko_config")
public class FakenekoConfigNeoForge {
	public static final Logger LOGGER = LoggerFactory.getLogger("fakeneko_config");

	public FakenekoConfigNeoForge(IEventBus eventBus) {
		TestConfig.init();
		LOGGER.info("Loaded fakeneko_config (NeoForge)");
	}
}
