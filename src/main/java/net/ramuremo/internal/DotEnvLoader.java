package net.ramuremo.internal;

import io.github.cdimascio.dotenv.Dotenv;

public class DotEnvLoader {
    private static Dotenv dotenv = null;

    public static void load() {
        dotenv = Dotenv.load();
    }

    public static String getDiscordToken() {
        if (dotenv == null) load();
        return dotenv.get("DISCORD_TOKEN");
    }
}
