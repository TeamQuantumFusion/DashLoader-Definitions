package dev.notalpha.dashloader;

import dev.notalpha.dashloader.io.Serializer;
import dev.notalpha.dashloader.io.data.CacheInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;

public final class DashLoader {
	public static final Logger LOG = LogManager.getLogger("DashLoader");
	public static final Serializer<CacheInfo> METADATA_SERIALIZER = new Serializer<>(CacheInfo.class);
	public static final String MOD_HASH;
	private static final String VERSION = FabricLoader.getInstance()
			.getModContainer("dashloader")
			.orElseThrow(() -> new IllegalStateException("DashLoader not found... apparently! WTF?"))
			.getMetadata()
			.getVersion()
			.getFriendlyString();

	static {
		ArrayList<ModMetadata> versions = new ArrayList<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModMetadata metadata = mod.getMetadata();
			versions.add(metadata);
		}

		versions.sort(Comparator.comparing(ModMetadata::getId));

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < versions.size(); i++) {
			ModMetadata metadata = versions.get(i);
			stringBuilder.append(i).append("$").append(metadata.getId()).append('&').append(metadata.getVersion().getFriendlyString());
		}

		MOD_HASH = DigestUtils.md5Hex(stringBuilder.toString()).toUpperCase();
	}

	private DashLoader() {
		LOG.info("Initializing DashLoader {}.", VERSION);
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			LOG.warn("DashLoader launched in dev.");
		}
	}

	@SuppressWarnings("EmptyMethod")
	public static void bootstrap() {
	}
}
