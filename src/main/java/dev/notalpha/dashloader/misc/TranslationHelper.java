package dev.notalpha.dashloader.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Language;

import java.util.HashMap;
import java.util.Objects;

public class TranslationHelper {
    private static final TranslationHelper INSTANCE = new TranslationHelper();
    private final HashMap<String, String> translations;
    private String langCode;

    private TranslationHelper() {
        this.translations = new HashMap<>();
    }

    public static TranslationHelper getInstance() {
        var langCode = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        if (!Objects.equals(INSTANCE.langCode, langCode)) {
            INSTANCE.langCode = langCode;
            INSTANCE.loadLang(langCode);
        }
        return INSTANCE;
    }

    private void loadLang(String langCode) {
        this.langCode = langCode;
        var stream = this.getClass().getClassLoader().getResourceAsStream("dashloader/lang/" + langCode + ".json");
        if (stream != null) {
            Language.load(stream, this.translations::put);
        } else {
            stream = this.getClass().getClassLoader().getResourceAsStream("dashloader/lang/en_us.json");
            if (stream != null) {
                Language.load(stream, this.translations::put);
            }
        }
    }

    public String get(String text) {
        return this.translations.getOrDefault(text, text);
    }

    public boolean has(String key) {
        return this.translations.containsKey(key);
    }
}
