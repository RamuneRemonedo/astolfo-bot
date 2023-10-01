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

    }

    public static void registerFeature(Feature feature) {
        for (CommandData command : feature.getCommands()) {
            final String name = command.getName();
            System.out.println(name);
            for (Method method : feature.getClass().getMethods()) {
                SubscribeCommand[] annotations = method.getAnnotationsByType(SubscribeCommand.class);
                if (annotations == null) continue;
                if (Arrays.stream(annotations).noneMatch(cmd -> cmd.name().equals(name))) throw new IllegalArgumentException("Feature doesn't contain match @SubscribeCommand method.");

            }
        }

        features.add(feature);
    }

    public static List<Feature> getFeatures() {
        return features;
    }

    public static void main(String[] args) {
        registerFeature(
                new Feature() {
                    @Override
                    public Set<CommandData> getCommands() {
                        return Set.of(
                                new CommandDataImpl(Command.Type.MESSAGE, "name")
                        );
                    }

                    @SubscribeCommand(name = "name")
                    public void onCommand(SlashCommandInteractionEvent event) {
                        event.getChannel();
                    }
                }
        );
    }
}
