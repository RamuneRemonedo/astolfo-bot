package net.ramuremo.internal;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Util {
    public static List<Command.Choice> toChoices(String... values) {
        final List<Command.Choice> choices = new ArrayList<>();
        for (String value : values) {
            choices.add(new Command.Choice(value, value));
        }
        return choices;
    }

    public static EmbedBuilder decorateEmbedBuilder() {
        return new EmbedBuilder()
                .setColor(new Color(0xFCAAD9))
                .setAuthor("Created by riya", "https://twitter.com/im_lottie_noble", Client.getClient().getSelfUser().getAvatarUrl());

    }

    public static void autoDelete(Message message) {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            try {
                delete(message);
            } catch (Exception ignored) {
            }
        }, 5, TimeUnit.SECONDS);
    }

    private static void delete(Message message) {
        message.delete().queue();
    }

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        String stackTrace = sw.toString();
        int endIndex = stackTrace.length() - 2000;

        return stackTrace.substring(0, Math.max(endIndex, 0));
    }

    public static long getCurrentUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public static <V> V random(V... values) {
        int i = new Random().nextInt(values.length);
        return values[i];
    }
}
