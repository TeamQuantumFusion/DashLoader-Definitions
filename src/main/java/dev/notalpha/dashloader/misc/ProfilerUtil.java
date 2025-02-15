package dev.notalpha.dashloader.misc;


public final class ProfilerUtil {
    public static long RELOAD_START = 0;

    public static String getTimeStringFromStart(long start) {
        return getTimeString(System.currentTimeMillis() - start);
    }

    public static String getTimeString(long ms) {
        if (ms >= 60000) { // 1m
            return ((int) ((ms / 60000))) + "m " + ((int) (ms % 60000) / 1000) + "s"; // [4m 42s]
        } else if (ms >= 3000) // 3s
        {
            return printMsToSec(ms) + "s"; // 1293ms = [1.2s]
        } else {
            return ms + "ms"; // [400ms]
        }
    }

    private static float printMsToSec(long ms) {
        return Math.round(ms / 100f) / 10f;
    }
}
