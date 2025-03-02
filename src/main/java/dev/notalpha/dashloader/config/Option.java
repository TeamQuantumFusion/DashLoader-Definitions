package dev.notalpha.dashloader.config;

public enum Option {
    CACHE_MODEL_LOADER(
        "cache.model",
        "Caches BakedModels which allows the game to load extremely fast"),
    CACHE_SPRITE_STITCHING(
        "cache.sprite.stitch",
        "Caches sprite stitching"),
    CACHE_SPRITE_CONTENT(
        "cache.sprite.content",
        "Caches sprite loading"),
    CACHE_FONT(
        "cache.font",
        "Caches all of the fonts and their images."),
    CACHE_SPLASH_TEXT(
        "cache.SplashTextResourceSupplierMixin",
        "Caches the splash texts from the main screen."),
    CACHE_SHADER(
        "cache.shader",
        "Caches the GL Shaders."),
    FAST_MODEL_IDENTIFIER_EQUALS(
        "misc.ModelIdentifierMixin",
        "Use a much faster .equals() on the ModelIdentifiers"),
    FAST_WALL_BLOCK(
        "WallBlockMixin",
        "Caches the 2 most common blockstates for wall blocks.");
    public final String mixinContains;
    public final String description;

    Option(String mixinContains, String description) {
        this.mixinContains = mixinContains;
        this.description = description;
    }
}
