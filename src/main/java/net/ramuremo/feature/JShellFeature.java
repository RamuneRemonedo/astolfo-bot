package net.ramuremo.feature;

import jdk.jshell.JShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.ramuremo.internal.CommandConsumer;
import net.ramuremo.internal.Feature;
import net.ramuremo.internal.Util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

public class JShellFeature implements Feature {

    private static String eval(String code) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(outputStream);
        JShell jShell = JShell.builder().out(stream).build();

        jShell.eval(code);
        jShell.close();

        String output = outputStream.toString();
        stream.close();
        outputStream.close();
        return output;
    }

    @Override
    public Set<CommandConsumer> getCommands() {
        return Set.of(
                new CommandConsumer(new CommandDataImpl("jshell", "jshellを実行します")
                        .addOption(OptionType.STRING, "code", "evalするコード", true)) {
                    @Override
                    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
                        if (event.getUser().getIdLong() != 430337593561841674L)
                            throw new PermissionException("あなたは実行できません");

                        OptionMapping codeOption = event.getOption("code");
                        if (codeOption == null) {

                            throw new IllegalArgumentException("コードがありません");

                        }
                        String code = codeOption.getAsString();

                        event.getHook().editOriginalEmbeds(
                                Util.decorateEmbedBuilder()
                                        .setDescription(eval(code))
                                        .build()
                        ).complete();
                    }
                }
        );
    }

    @SubscribeEvent
    public void onMessageRecieve(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();

        if (event.getAuthor().getIdLong() != 430337593561841674L) return;
        if (!content.startsWith(">>")) return;

        try {
            event.getMessage().replyEmbeds(
                    Util.decorateEmbedBuilder()
                            .setDescription(eval(content.replace(">>", "")))
                            .build()
            ).queue();
        } catch (Exception e) {
            event.getMessage().replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("インテラクション中にエラーが発生しました!")
                            .setDescription("例外が起きました! - " + e.getMessage())
                            .addField("スタックトレーっす", Util.getStackTrace(e), true)
                            .build()
            ).queue();
        }
    }
}
