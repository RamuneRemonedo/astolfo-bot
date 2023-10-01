package net.ramuremo.internal;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.lang.reflect.Method;
import java.util.*;

public class FeatureHandler {
    private static List<Feature> features = new ArrayList<>();

    public static void registerFeature(Feature... features) {
        for (Feature feature : features) {
            registerFeature(feature);
        }
    }

    public static void registerFeature(Feature feature) {
        Client.getClient().addEventListener(feature);
        feature.getCommands().forEach(command -> Client.getClient().upsertCommand(command.getData()).queue());
        features.add(feature);
    }

    public static List<Feature> getFeatures() {
        return features;
    }
}
