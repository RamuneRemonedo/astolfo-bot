package net.ramuremo.internal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.ramuremo.feature.*;

import java.util.Arrays;
import java.util.Objects;

public class Client {
    private static JDA client = null;

    public static JDA getClient() {
        return client;
    }

    public static void run(String token) {
        client = JDABuilder.createDefault(token, Arrays.asList(GatewayIntent.values()))
                .setEventManager(new AnnotatedEventManager())
                .build();
        try {
            client.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        client.getPresence().setPresence(OnlineStatus.IDLE, Activity.playing("/help | https://discord.gg/3tCEZvp2E9"));
        FeatureHandler.registerCommandHandleListener();

        FeatureHandler.registerFeature(
                new AstolfoFeature(),
                new AvatarFeature(),
                new TextFeature(),
                new JShellFeature(),
                new NotifyBumpFeature()
        );

        Objects.requireNonNull(client.getTextChannelById(1143892440353353829L)).sendMessage("起動できた!!!" + (isDebuggerActive() ? "デバッグモードとして" : "")).complete();
        System.out.println("Login as " + client.getSelfUser().getName());
    }

    private static boolean isDebuggerActive() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    }
}
