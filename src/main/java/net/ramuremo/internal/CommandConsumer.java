package net.ramuremo.internal;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandConsumer {

    private final CommandData data;

    public CommandConsumer(CommandData data) {
        this.data = data;
    }

    public CommandData getData() {
        return data;
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
    }

    public void onMessageContextInteraction(MessageContextInteractionEvent event) throws Exception {
    }
}
