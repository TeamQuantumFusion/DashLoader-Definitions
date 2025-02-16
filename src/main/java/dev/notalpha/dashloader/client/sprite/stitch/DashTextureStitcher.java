package dev.notalpha.dashloader.client.sprite.stitch;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DashTextureStitcher<T extends TextureStitcher.Stitchable> extends TextureStitcher<T> {
    @Nullable
    private ExportedData<T> data;
    private int remainingSlots;

    public DashTextureStitcher(int maxWidth, int maxHeight, int mipLevel, @Nullable ExportedData<T> data) {
        super(maxWidth, maxHeight, mipLevel);
        this.data = data;
        this.remainingSlots = data == null ? 0 : data.slots.size();
    }

    @Override
    public int getWidth() {
        if (this.data == null) {
            return super.getWidth();
        }
        return data.width;
    }

    @Override
    public int getHeight() {
        if (this.data == null) {
            return super.getHeight();
        }
        return data.height;
    }

    @Override
    public void add(T info) {
        if (data == null) {
            super.add(info);
            return;
        }

        // If it starts recaching, doRecache will re-add the entries to the list.
        var id = info.getId();
        var slot = data.slots.get(id);
        if (slot == null) {
            DashLoader.LOG.warn("Sprite {} was not cached last time.", id);

            doFallback();
            // This was never added to the slot, so it would not get added to super.
            this.add(info);
            return;
        }

        if (slot.contents != null) {
            DashLoader.LOG.warn("Sprite {} was added twice??", id);
        }

        remainingSlots -= 1;
        slot.contents = info;

        if (slot.width != info.getWidth() || slot.height != info.getHeight()) {
            DashLoader.LOG.warn("Sprite {} had changed dimensions since last launch, falling back.", id);
            doFallback();
        }
    }

    public void doFallback() {
        if (data != null) {
            DashLoader.LOG.error("Using fallback on texture stitcher.");
            var slots = data.slots;
            data = null;
            slots.forEach((identifier, tDashTextureSlot) -> {
                if (tDashTextureSlot.contents != null) {
                    this.add(tDashTextureSlot.contents);
                }
            });
        } else {
            DashLoader.LOG.error("Tried to fallback stitcher twice.");
        }
    }

    @Override
    public void stitch() {
        if (data != null && remainingSlots != 0) {
            DashLoader.LOG.warn("Remaining slots did not match the cached amount, Falling back.");
            data.slots.forEach((identifier, tDashTextureSlot) -> {
                if (tDashTextureSlot.contents == null) {
                    DashLoader.LOG.error("Sprite {} was not requested", identifier);
                }
            });
            doFallback();
        }

        if (data == null) {
            super.stitch();
        }
    }

    @Override
    public void getStitchedSprites(SpriteConsumer<T> consumer) {
        if (data == null) {
            super.getStitchedSprites(consumer);
        } else {
            data.slots.forEach((identifier, dashTextureSlot) -> consumer.load(dashTextureSlot.contents, dashTextureSlot.x, dashTextureSlot.y));
        }
    }

    public static class Data<T extends TextureStitcher.Stitchable> {
        public final IntObjectList<DashTextureSlot<T>> slots;
        public final int width;
        public final int height;

        public Data(IntObjectList<DashTextureSlot<T>> slots, int width, int height) {
            this.slots = slots;
            this.width = width;
            this.height = height;
        }

        public Data(RegistryWriter factory, TextureStitcher<T> stitcher) {
            this.slots = new IntObjectList<>();
            stitcher.getStitchedSprites((info, x, y) -> this.slots.put(factory.add(info.getId()), new DashTextureSlot<>(x, y, info.getWidth(), info.getHeight())));
            this.width = stitcher.getWidth();
            this.height = stitcher.getHeight();
        }

        public ExportedData<T> export(RegistryReader reader) {
            var output = new HashMap<Identifier, DashTextureSlot<T>>();
            this.slots.forEach((key, value) -> output.put(reader.get(key), value));

            return new ExportedData<>(
                output,
                width,
                height
            );
        }
    }

    public static class ExportedData<T extends TextureStitcher.Stitchable> {
        public final Map<Identifier, DashTextureSlot<T>> slots;
        public final int width;
        public final int height;

        public ExportedData(Map<Identifier, DashTextureSlot<T>> slots, int width, int height) {
            this.slots = slots;
            this.width = width;
            this.height = height;
        }
    }
}
