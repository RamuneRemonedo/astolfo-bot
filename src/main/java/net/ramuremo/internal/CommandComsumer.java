package net.ramuremo.internal;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.function.Consumer;

public interface CommandComsumer extends Consumer<SlashCommandInteractionEvent> {
}
