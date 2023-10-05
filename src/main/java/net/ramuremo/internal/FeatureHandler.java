package net.ramuremo.internal;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class FeatureHandler {
    private static final List<Feature> features = new ArrayList<>();

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

    static void registerCommandHandleListener() {
        Client.getClient().addEventListener(new CommandHandleListener());
    }

    public static List<Feature> getFeatures() {
        return features;
    }

    private static class CommandHandleListener {
        @SubscribeEvent
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            String name = event.getName();
            for (Feature feature : features) {
                for (CommandConsumer command : feature.getCommands()) {
                    if (command.getData().getName().equals(name)) {
                        event.deferReply().complete();
                        try {
                            command.onSlashCommandInteraction(event);
                        } catch (Exception e) {
                            event.getHook().editOriginalEmbeds(
                                    new EmbedBuilder()
                                            .setTitle("インテラクション中にエラーが発生しました!")
                                            .setDescription("例外が起きました! - " + e.getMessage())
                                            .addField("スタックトレーっす", Util.getStackTrace(e), true)
                                            .build()
                            ).complete();
                        }
                        return;
                    }
                }
            }
        }

        @SubscribeEvent
        public void onMessageContextInteraction(MessageContextInteractionEvent event) {
            String name = event.getName();
            for (Feature feature : features) {
                for (CommandConsumer command : feature.getCommands()) {
                    if (command.getData().getName().equals(name)) {
                        event.deferReply().complete();
                        try {
                            command.onMessageContextInteraction(event);
                        } catch (Exception e) {
                            event.getHook().editOriginalEmbeds(
                                    new EmbedBuilder()
                                            .setTitle("インテラクション中にエラーが発生しました!")
                                            .setDescription("例外が起きました! - " + e.getMessage())
                                            .addField("スタックトレーっす", Util.getStackTrace(e), true)
                                            .build()
                            ).complete();
                        }
                        return;
                    }
                }
            }
        }
    }
}
