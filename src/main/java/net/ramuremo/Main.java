package net.ramuremo;

import net.ramuremo.internal.Client;
import net.ramuremo.internal.DBHandler;
import net.ramuremo.internal.DotEnvLoader;

public class Main {
    public static void main(String[] args) {
        DotEnvLoader.load();
        DBHandler.connect(DotEnvLoader.getDBConnectionString());
        Client.run(DotEnvLoader.getDiscordToken());
    }
}