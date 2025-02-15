package dev.notalpha.dashloader.misc;

import dev.notalpha.dashloader.config.Config;
import dev.notalpha.dashloader.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;

public final class HahaManager {
    private static final String[] FACTS = {
        "Dash was for the cool kids",
        "fun fact: 1 + 1 = 11",
        "glisco goes around and yells",
        ":froge:",
        ":bigfroge:",
        ":smolfroge:",
        "Frog + Doge = Froge",
        "Froges dad is cool",
        "Rogger Rogger!",
        "Yes commander!",
        "I am not the swarm!",
        "Get that golden strawberry!",
        "Kevin is cool.",
        "B-Sides are where I flex.",
        "Starting an accelerated backhop",
        "Gordon Freeman. I like your tie.",
        "The factory must grow.",
        "Not the biters.",
        "Ya got more red belts?",
        "I need more boilers.",
        "Throughput of circuits is gud.",
        "amogus",
        "sus",
        "imposter",
        "it was red!",
        "What does the vent button do?",
        "We need more white wine.",
        "I season my cuttingboard",
        "Do as I say, not as I do",
        "Colton is fired",
        "Was a banger on the cord.",
        "My code thinks different.",
        "Make it for 300$ sell it for 1300$",
        "Steve is almost chad",
        "IKEA is traditional.",
        "1 + 1 = 11",
        "https://ko-fi.com/notequalalpha",
        "USB-C is gud.",
        "Modrinth gud.",
        "Leocth and Alpha were first.",
        "Corn on a jakob is the best.",
        "Cornebb is cool.",
        "Hyphen is cool.",
        "DashLoader kinda banger.",
        "MFOTS was a thing.",
        ":tnypotat:",
        "418 I'm a teapot is a real error",
        "mld hrdr - leocth 2022",
        // HiItsDevin
        "Devin beat 7C after 5 1/2 hours",
        // shedaniel
        "Look at me, I am vibing up here",
        "Doesn't break REI",
        // devonk15
        "Come here often?"
    };

    public static String getFact() {
        Config config = ConfigHandler.INSTANCE.config;
        List<String> splashLines = new ArrayList<>(config.customSplashLines);
        if (config.addDefaultSplashLines) {
            splashLines.addAll(List.of(FACTS));
        }

        if (splashLines.isEmpty()) {
            return null;
        }


        return splashLines.get((int) (System.currentTimeMillis() % splashLines.size()));
    }
}
