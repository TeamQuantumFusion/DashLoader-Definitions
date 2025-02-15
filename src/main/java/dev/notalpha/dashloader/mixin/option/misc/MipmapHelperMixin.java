package dev.notalpha.dashloader.mixin.option.misc;

import dev.notalpha.dashloader.misc.UnsafeImage;
import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MipmapHelper.class)
public abstract class MipmapHelperMixin {
    @Shadow
    private static int getColorComponent(int one, int two, int three, int four, int bits) {
        return 0;
    }

    @Shadow
    private static float getColorFraction(int value) {
        return 0;
    }

    @Shadow
    private static boolean hasAlpha(NativeImage image) {
        return false;
    }

    @Shadow
    private static int blend(int one, int two, int three, int four, boolean checkAlpha) {
        return 0;
    }

    /**
     * @author notalpha
     * @reason fast
     */
    @Overwrite
    public static NativeImage[] getMipmapLevelsImages(NativeImage[] originals, int mipmap) {
        if (mipmap + 1 <= originals.length) {
            return originals;
        }

        UnsafeImage[] images = new UnsafeImage[mipmap + 1];
        images[0] = new UnsafeImage(originals[0]);
        UnsafeImage baseImage = images[0];
        boolean hasAlpha = false;

        hi:
        for (int y = 0; y < baseImage.height; ++y) {
            for (int x = 0; x < baseImage.width; ++x) {
                if (baseImage.get(x, y) >> 24 == 0) {
                    hasAlpha = true;
                    break hi;
                }
            }
        }

        for (int layer = 1; layer <= mipmap; ++layer) {
            if (layer < originals.length) {
                images[layer] = new UnsafeImage(originals[layer]);
            } else {
                UnsafeImage sourceImage = images[layer - 1];
                UnsafeImage targetImage = new UnsafeImage(new NativeImage(sourceImage.width >> 1, sourceImage.height >> 1, false));
                int height = targetImage.height;
                int width = targetImage.width;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int one = sourceImage.get(x * 2, y * 2);
                        int two = sourceImage.get(x * 2 + 1, y * 2);
                        int three = sourceImage.get(x * 2, y * 2 + 1);
                        int four = sourceImage.get(x * 2 + 1, y * 2 + 1);

                        if (hasAlpha) {
                            float a = 0.0F;
                            float r = 0.0F;
                            float g = 0.0F;
                            float b = 0.0F;
                            if (one >> 24 != 0) {
                                a += getColorFraction(one >> 24);
                                r += getColorFraction(one >> 16);
                                g += getColorFraction(one >> 8);
                                b += getColorFraction(one);
                            }

                            if (two >> 24 != 0) {
                                a += getColorFraction(two >> 24);
                                r += getColorFraction(two >> 16);
                                g += getColorFraction(two >> 8);
                                b += getColorFraction(two);
                            }

                            if (three >> 24 != 0) {
                                a += getColorFraction(three >> 24);
                                r += getColorFraction(three >> 16);
                                g += getColorFraction(three >> 8);
                                b += getColorFraction(three);
                            }

                            if (four >> 24 != 0) {
                                a += getColorFraction(four >> 24);
                                r += getColorFraction(four >> 16);
                                g += getColorFraction(four >> 8);
                                b += getColorFraction(four);
                            }

                            a /= 4.0F;
                            r /= 4.0F;
                            g /= 4.0F;
                            b /= 4.0F;
                            int aI = (int) (Math.pow(a, 0.45454545454545453) * 255.0);
                            int rI = (int) (Math.pow(r, 0.45454545454545453) * 255.0);
                            int gI = (int) (Math.pow(g, 0.45454545454545453) * 255.0);
                            int bI = (int) (Math.pow(b, 0.45454545454545453) * 255.0);

                            if (aI < 96) {
                                aI = 0;
                            }

                            targetImage.set(x, y, aI << 24 | rI << 16 | gI << 8 | bI);
                        } else {
                            int a = getColorComponent(one, two, three, four, 24);
                            int r = getColorComponent(one, two, three, four, 16);
                            int g = getColorComponent(one, two, three, four, 8);
                            int b = getColorComponent(one, two, three, four, 0);
                            targetImage.set(x, y, a << 24 | r << 16 | g << 8 | b);
                        }
                    }
                }

                images[layer] = targetImage;
            }
        }

        NativeImage[] imagesOut = new NativeImage[mipmap + 1];
        for (int i = 0; i < imagesOut.length; i++) {
            imagesOut[i] = images[i].image;
        }
        return imagesOut;
    }
}
