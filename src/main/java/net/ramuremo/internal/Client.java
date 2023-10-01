package net.ramuremo.internal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Client {
    private static JDA client = null;

    public static JDA getClient() {
        return client;
    }

    public static void run(String token) {
        client = JDABuilder.createDefault(token, Arrays.asList(GatewayIntent.values()))
                .setEventManager(new AnnotatedEventManager())
                .build();
    }

    private static void registerFeatures() {

    }
}
