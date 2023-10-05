package net.ramuremo.internal;

import java.util.Set;

public interface Feature {
    Set<CommandConsumer> getCommands();
}
