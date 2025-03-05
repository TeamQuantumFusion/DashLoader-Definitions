package dev.notalpha.dashloader.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.notalpha.dashloader.DashLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumMap;

public class ConfigHandler {
	private static final EnumMap<Option, Boolean> OPTION_ACTIVE = new EnumMap<>(Option.class);
	private static final String DISABLE_OPTION_TAG = "dashloader:disableoption";

	static {
		for (Option value : Option.values()) {
			OPTION_ACTIVE.put(value, true);
		}
	}
	public static final ConfigHandler INSTANCE = new ConfigHandler(FabricLoader.getInstance().getConfigDir().normalize().resolve("dashloader.json"));

	private final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private final Path configPath;
	public Config config = new Config();

	public ConfigHandler(Path configPath) {
		this.configPath = configPath;
		this.reloadConfig();
		this.config.options.forEach((s, aBoolean) -> {
			try {
				var option = Option.valueOf(s.toUpperCase());
				OPTION_ACTIVE.put(option, aBoolean);
				if (!aBoolean) {
					DashLoader.LOG.warn("Disabled Optional Feature {} from DashLoader config.", s);
				}
			} catch (IllegalArgumentException illegalArgumentException) {
				DashLoader.LOG.error("Could not disable Optional Feature {} from DashLoader config as it does not exist.", s);
			}
		});

		for (var modContainer : FabricLoader.getInstance().getAllMods()) {
			var mod = modContainer.getMetadata();
			if (mod.containsCustomValue(DISABLE_OPTION_TAG)) {
				for (var value : mod.getCustomValue(DISABLE_OPTION_TAG).getAsArray()) {
					final String feature = value.getAsString();
					try {
						var option = Option.valueOf(feature.toUpperCase());
						OPTION_ACTIVE.put(option, false);
						DashLoader.LOG.warn("Disabled Optional Feature {} from {} config. {}", feature, mod.getId(), mod.getName());
					} catch (IllegalArgumentException illegalArgumentException) {
						DashLoader.LOG.error("Could not disable Optional Feature {} from {} config as it does not exist. {}", feature, mod.getId(), mod.getName());
					}
				}
			}
		}
	}

	public static boolean shouldApplyMixin(String name) {
		for (Option value : Option.values()) {
			if (name.contains(value.mixinContains)) {
				return OPTION_ACTIVE.get(value);
			}
		}
		return true;
	}

	public static boolean optionActive(Option option) {
		return OPTION_ACTIVE.get(option);
	}

	public void reloadConfig() {
		try {
			if (Files.exists(this.configPath)) {
				final BufferedReader json = Files.newBufferedReader(this.configPath);
				this.config = this.gson.fromJson(json, Config.class);
				json.close();
			}
		} catch (Throwable err) {
			DashLoader.LOG.info("Config corrupted creating a new one.", err);
		}

		this.saveConfig();
	}

	public void saveConfig() {
		try {
			Files.createDirectories(this.configPath.getParent());
			Files.deleteIfExists(this.configPath);
			final BufferedWriter writer = Files.newBufferedWriter(this.configPath, StandardOpenOption.CREATE);
			this.gson.toJson(this.config, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
