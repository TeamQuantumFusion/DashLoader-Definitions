package dev.notalpha.dashloader.client.ui.widget;

import dev.notalpha.dashloader.misc.TranslationHelper;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigListWidget extends ElementListWidget<ConfigListWidget.Entry> {
    public static final int INPUT_FIELD_WIDTH = 75;
    public static final int RESET_BUTTON_WIDTH = 50;
    private final TranslationHelper translations = TranslationHelper.getInstance();
    private int longestLabel;

    public ConfigListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    @SuppressWarnings("UnusedReturnValue")
    public int addCategory(String label) {
        return addEntry(new CategoryEntry(label));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int addBoolToggle(String label, boolean value, boolean defaultValue, BooleanConsumer saveCallback) {
        return addEntry(new BoolConfigEntry(label, value, defaultValue, saveCallback));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int addIntSlider(String label, int value, int defaultValue, int min, int max, IntConsumer saveCallback) {
        return addEntry(new IntSliderConfigEntry(label, value, defaultValue, min, max, saveCallback));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int addTextField(String label, String value, String defaultValue, Consumer<String> saveCallback) {
        return addEntry(new TextFieldEntry(label, value, defaultValue, c -> true, saveCallback));
    }

    @SuppressWarnings("UnusedReturnValue")
    public int addIntField(String label, int value, int defaultValue, IntConsumer saveCallback) {
        return addEntry(new IntFieldEntry(label, value, defaultValue, saveCallback));
    }

    public void saveValues() {
        this.children().forEach(child -> {
            if (child instanceof ConfigEntry<?> entry) {
                entry.saveValue();
            }
        });
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    public void update() {
        this.children().forEach(Entry::update);
    }

    public abstract class Entry extends ElementListWidget.Entry<Entry> {
        public Text label;

        Entry(String label) {
            this.label = Text.of(translations.get(label));

            var len = ConfigListWidget.this.client.textRenderer.getWidth(this.label);
            if (len > ConfigListWidget.this.longestLabel) {
                ConfigListWidget.this.longestLabel = len;
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }

        abstract void update();
    }

    class CategoryEntry extends Entry {
        CategoryEntry(String label) {
            super(label);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(ConfigListWidget.this.client.textRenderer, this.label, x + (entryWidth - ConfigListWidget.this.client.textRenderer.getWidth(label)) / 2, y + (entryHeight - 5) / 2, 0xFFFFFF, false);
        }

        @Override
        void update() {
        }
    }

    abstract class ConfigEntry<T> extends Entry {
        protected final T defaultValue;
        public ClickableWidget widget;
        public ButtonWidget resetButton;
        protected T value;
        protected Tooltip tooltip;
        protected Consumer<T> saveFunc;

        ConfigEntry(String label, T value, T defaultValue, Consumer<T> saveCallback) {
            super(label);
            this.value = value;
            this.defaultValue = defaultValue;
            this.saveFunc = saveCallback;
            if (translations.has(label + ".tooltip")) {
                this.tooltip = Tooltip.of(Text.of(translations.get(label + ".tooltip")));
            }

            this.resetButton = new ButtonWidget.Builder(Text.of("Reset"), button -> {
                this.value = this.defaultValue;
                this.updateWidgetText();
                ConfigListWidget.this.update();
            }).width(RESET_BUTTON_WIDTH).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(ConfigListWidget.this.client.textRenderer, this.label, x + 90 - ConfigListWidget.this.longestLabel, y + (entryHeight - ConfigListWidget.this.client.textRenderer.fontHeight) / 2, 0xFFFFFF, false);

            this.widget.setPosition(x + entryWidth - INPUT_FIELD_WIDTH - RESET_BUTTON_WIDTH - 5, y);
            this.widget.render(context, mouseX, mouseY, tickDelta);
            this.resetButton.setPosition(x + entryWidth - RESET_BUTTON_WIDTH, y);
            this.resetButton.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.widget, this.resetButton);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.widget, this.resetButton);
        }

        @Override
        void update() {
            this.resetButton.active = !Objects.equals(this.value, this.defaultValue);
        }

        public T getValue() {
            return this.value;
        }

        public void saveValue() {
            this.saveFunc.accept(this.value);
        }

        abstract void updateWidgetText();
    }

    class BoolConfigEntry extends ConfigEntry<Boolean> {
        BoolConfigEntry(String label, boolean value, boolean defaultValue, BooleanConsumer saveCallback) {
            super(label, value, defaultValue, saveCallback);

            this.widget = new ButtonWidget.Builder(ScreenTexts.onOrOff(this.value), button -> {
                this.value = !(boolean) this.value;
                updateWidgetText();
                ConfigListWidget.this.update();
            }).width(INPUT_FIELD_WIDTH).build();

            this.widget.setTooltip(this.tooltip);
        }

        @Override
        void updateWidgetText() {
            this.widget.setMessage(ScreenTexts.onOrOff(this.value));
        }
    }

    class IntSliderConfigEntry extends ConfigEntry<Integer> {
        private final int min;
        private final int max;

        IntSliderConfigEntry(String label, int value, int defaultValue, int min, int max, IntConsumer saveCallback) {
            super(label, value, defaultValue, saveCallback);
            this.min = min;
            this.max = max;

            this.widget = new SliderThing(0, 0, INPUT_FIELD_WIDTH, ConfigListWidget.this.itemHeight - 2, Text.of(String.valueOf(value)), min, max, (double) (this.value - min) / (max - min));
            this.widget.setTooltip(this.tooltip);
        }

        @Override
        void updateWidgetText() {
            ((SliderThing) this.widget).setValue((double) (this.value - this.min) / (this.max - this.min));
        }

        public class SliderThing extends SliderWidget {
            private final double min;
            private final double max;

            public SliderThing(int x, int y, int width, int height, Text message, double min, double max, double value) {
                super(x, y, width, height, message, value);
                this.min = min;
                this.max = max;
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Text.of(String.valueOf((int) this.getValue())));
            }

            @Override
            protected void applyValue() {
                IntSliderConfigEntry.this.value = (int) this.getValue();
                ConfigListWidget.this.update();
            }

            public double getValue() {
                return this.value * (max - min) + min;
            }

            public void setValue(double value) {
                this.value = value;
                updateMessage();
                applyValue();
            }
        }
    }

    class TextFieldEntry extends ConfigEntry<String> {
        CharPredicate filter;

        TextFieldEntry(String label, String value, String defaultValue, CharPredicate filter, Consumer<String> saveCallback) {
            super(label, value, defaultValue, saveCallback);
            this.filter = filter;

            this.widget = new TextFieldWidget(ConfigListWidget.this.client.textRenderer, 0, 0, INPUT_FIELD_WIDTH, ConfigListWidget.this.itemHeight - 2, Text.empty()) {
                @Override
                public boolean charTyped(char chr, int modifiers) {
                    if (TextFieldEntry.this.filter.test(chr)) {
                        return super.charTyped(chr, modifiers);
                    }
                    return false;
                }
            };

            var textWidget = (TextFieldWidget) widget;
            textWidget.setMaxLength(Integer.MAX_VALUE);
            textWidget.setText(String.valueOf(this.value));
            textWidget.setChangedListener(text -> {
                this.value = text;
                ConfigListWidget.this.update();
            });

            textWidget.setTooltip(this.tooltip);
        }

        @Override
        void updateWidgetText() {
            ((TextFieldWidget) this.widget).setText(this.value);
        }
    }

    class IntFieldEntry extends TextFieldEntry {
        IntFieldEntry(String label, int value, int defaultValue, IntConsumer saveCallback) {
            super(label, String.valueOf(value), String.valueOf(defaultValue), null, str -> saveCallback.accept(Integer.parseInt(str)));

            var textWidget = (TextFieldWidget) this.widget;
            this.filter = chr -> chr >= '0' && chr <= '9';
            textWidget.setChangedListener(text -> {
                this.value = text.isEmpty() ? "0" : text;
                ConfigListWidget.this.update();
            });

            this.widget.setTooltip(this.tooltip);
        }

        @Override
        void updateWidgetText() {
            ((TextFieldWidget) this.widget).setText(String.valueOf(this.value));
        }
    }
}
