package net.ramuremo.internal;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.function.Consumer;

public class CommandConsumer implements Consumer<SlashCommandInteractionEvent> {

    private final CommandData data;

    public CommandConsumer(CommandData data) {
        this.data = data;
    }

    public CommandData getData() {
        return data;
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
    }
}
