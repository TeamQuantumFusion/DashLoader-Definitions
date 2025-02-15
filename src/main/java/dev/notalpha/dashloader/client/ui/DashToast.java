package dev.notalpha.dashloader.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.notalpha.dashloader.misc.HahaManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class DashToast implements Toast {
    private static final int PROGRESS_BAR_HEIGHT = 2;
    private static final int PADDING = 8;
    private static final int LINES = 125;
    public final DashToastState state;
    private final Random random = new Random();
    @Nullable
    private final String fact = HahaManager.getFact();
    private List<Line> lines = new ArrayList<>();
    private long oldTime = System.currentTimeMillis();

    public DashToast() {
        this.state = new DashToastState();
        // Create lines
        for (int i = 0; i < LINES; i++) {
            this.lines.add(new Line());
        }
    }

    private static void drawVertex(Matrix4f m4f, BufferBuilder bb, float z, float x, float y, Color color) {
        bb.vertex(m4f, x, y, z).color(color.red(), color.green(), color.blue(), color.alpha()).next();
    }

    public int getWidth() {
        return 200;
    }

    public int getHeight() {
        return 40;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int barY = height - PROGRESS_BAR_HEIGHT;

        // Get progress
        final float progress;
        final Color progressColor;
        if (state.getStatus() == DashToastStatus.CRASHED) {
            progress = (float) this.state.getProgress();
            progressColor = DrawerUtil.FAILED_COLOR;
        } else {
            progress = (float) this.state.getProgress();
            progressColor = DrawerUtil.getProgressColor(progress);
        }

        // Tick progress
        List<Line> newList = new ArrayList<>();
        List<Line> newListPrio = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (Line line : this.lines) {
            if (line.tick(width, height, progress, (currentTime - this.oldTime) / 17f)) {
                newListPrio.add(line);
            } else {
                newList.add(line);
            }
        }
        this.oldTime = currentTime;
        this.lines = newList;
        this.lines.addAll(newListPrio);

        // Setup scissor
        MatrixStack matrices = context.getMatrices();
        {
            Vector4f vec = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
            vec.mul(matrices.peek().getPositionMatrix());
            Window window = manager.getClient().getWindow();
            double scale = window.getScaleFactor();
            RenderSystem.enableScissor(
                (int) (vec.x * scale),
                (int) (window.getFramebufferHeight() - (vec.y * scale) - height * scale),
                (int) (width * scale),
                (int) (height * scale));
        }

        // Draw the ui
        DrawerUtil.drawRect(context, 0, 0, width, height, DrawerUtil.BACKGROUND_COLOR);

        // Draw the background lines.
        this.drawBatched(matrices, (matrix4f, bufferBuilder) -> {
            for (Line line : lines) {
                line.draw(matrix4f, bufferBuilder);
            }
        });

        TextRenderer textRenderer = manager.getClient().textRenderer;
        // Draw progress text
        String progressText = this.state.getProgressText();
        int progressTextY = this.fact != null ? barY - PADDING : (barY / 2) + (textRenderer.fontHeight / 2);
        DrawerUtil.drawText(context, textRenderer, PADDING, progressTextY, this.state.getText(), DrawerUtil.STATUS_COLOR);
        DrawerUtil.drawText(context, textRenderer, (width - PADDING) - textRenderer.getWidth(progressText), progressTextY, progressText, DrawerUtil.STATUS_COLOR);

        if (this.fact != null) {
            // Draw the fun fact
            DrawerUtil.drawText(context, textRenderer, PADDING, textRenderer.fontHeight + PADDING, this.fact, DrawerUtil.FOREGROUND_COLOR);
        }

        // Draw progress bar
        DrawerUtil.drawRect(context, 0, barY, width, PROGRESS_BAR_HEIGHT, DrawerUtil.PROGRESS_TRACK);
        DrawerUtil.drawRect(context, 0, barY, (int) (width * progress), PROGRESS_BAR_HEIGHT, progressColor);

        // Epic rtx graphics. aka i slapped some glow on the things.
        this.drawBatched(matrices, (matrix4f, bb) -> {
            // Line glow
            for (Line line : lines) {
                line.drawGlow(matrix4f, bb);
            }
            // Progress bar glow
            DrawerUtil.drawGlow(matrix4f, bb, 0, barY, (int) (width * progress), PROGRESS_BAR_HEIGHT, 0.75f, progressColor, true, true, true, true);
        });
        RenderSystem.disableScissor();

        if (state.getStatus() == DashToastStatus.CRASHED && System.currentTimeMillis() - state.getTimeDone() > 10000) {
            return Visibility.HIDE;
        }

        if (state.getStatus() == DashToastStatus.DONE && System.currentTimeMillis() - state.getTimeDone() > 2000) {
            return Visibility.HIDE;
        }
        return Visibility.SHOW;
    }

    private void drawBatched(MatrixStack ms, BiConsumer<Matrix4f, BufferBuilder> consumer) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = ms.peek().getPositionMatrix();
        consumer.accept(matrix, bufferBuilder);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public enum ColorKind {
        Neutral,
        Progress,
        Crashed,
    }

    private final class Line {
        public final int width;
        public final int height;
        public ColorKind colorKind;
        public float x;
        public float y;
        public float speedBoost;
        private Color color;

        public Line() {
            this.x = -1000;
            this.y = -1000;
            this.width = DashToast.this.random.nextInt(30, 50);
            this.height = DashToast.this.random.nextInt(2, 5);
            this.colorKind = ColorKind.Neutral;
            this.color = new Color(0xFF0000FF);
        }

        public boolean tick(int screenWidth, int screenHeight, float progress, float delta) {
            // Move the values
            this.x += (float) (speedBoost * (0.8 + (2.5 * progress))) * delta;

            // Check if not visible
            if (x > screenWidth || x + width < 0) {
                // Randomize position
                this.x = -width;
                this.y = screenHeight * DashToast.this.random.nextFloat();

                // Randomise color
                if (state.getStatus() == DashToastStatus.CRASHED) {
                    if (DashToast.this.random.nextFloat() > 0.9 || this.colorKind == ColorKind.Progress) {
                        this.colorKind = ColorKind.Crashed;
                    }
                } else {
                    if (DashToast.this.random.nextFloat() > 0.95) {
                        this.colorKind = ColorKind.Progress;
                    } else {
                        this.colorKind = ColorKind.Neutral;
                    }
                }

                // Randomise speed based on some values.
                // Weight (the size of the line), 0.2 deviation
                float weight = 1f - getWeight();
                float weightSpeed = (float) (0.7 + (weight * 0.6));

                // Kind (The type of line),
                float kindSpeed;
                if (this.colorKind == ColorKind.Neutral) {
                    kindSpeed = (float) (1.0 + (DashToast.this.random.nextFloat() * 0.2f));
                } else {
                    kindSpeed = (float) (1.0 + (DashToast.this.random.nextFloat() * 0.8f));
                }

                this.speedBoost = kindSpeed * weightSpeed;
                return this.colorKind != ColorKind.Neutral;
            }
            this.color = getColor(progress);

            return false;
        }

        public void draw(Matrix4f b4, BufferBuilder bb) {
            Color end = DrawerUtil.withOpacity(color, 0f);
            drawVertex(b4, bb, 0f, x + width, y, color); // right top
            drawVertex(b4, bb, 0f, x, y, end); // left top
            drawVertex(b4, bb, 0f, x, y + height, end); // left bottom
            drawVertex(b4, bb, 0f, x + width, y + height, color); // right bottom
        }

        public void drawGlow(Matrix4f b4, BufferBuilder bb) {
            if (this.colorKind != ColorKind.Neutral) {
                DrawerUtil.drawGlow(b4, bb, x, y, width, height, (getWeight() + 2.0f) / 3.0f, this.color, false, true, false, true);
            }
        }

        public Color getColor(double progress) {
            Color color = switch (this.colorKind) {
                case Neutral -> DrawerUtil.NEUTRAL_LINE;
                case Progress -> {
                    if (state.getStatus() == DashToastStatus.CRASHED) {
                        yield DrawerUtil.FAILED_COLOR;
                    }

                    yield DrawerUtil.getProgressColor(progress);
                }
                case Crashed -> DrawerUtil.FAILED_COLOR;
            };

            return DrawerUtil.withOpacity(color, MathHelper.clamp(((this.x) / (this.width)), 0.0f, 1.0f));
        }

        public float getWeight() {
            return ((this.width * (float) this.height) - 60f) / 190f;
        }
    }
}
