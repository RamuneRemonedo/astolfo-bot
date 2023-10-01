package net.ramuremo.internal;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Set;

public interface Feature {
    Set<CommandConsumer> getCommands();
}
