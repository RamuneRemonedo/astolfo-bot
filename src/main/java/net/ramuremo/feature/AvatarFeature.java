package net.ramuremo.feature;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.ramuremo.internal.CommandConsumer;
import net.ramuremo.internal.Feature;
import net.ramuremo.internal.Util;

import java.util.Set;

public class AvatarFeature implements Feature {
    @Override
    public Set<CommandConsumer> getCommands() {
        return Set.of(
                new CommandConsumer(new CommandDataImpl("avatar", "ユーザーのアイコンを取得します")
                        .addOption(OptionType.USER, "target-user", "取得するユーザー", true)) {

                    @Override
                    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
                        OptionMapping option = event.getOption("target-user");

                        if (option == null) {

                            event.getHook().editOriginal("ユーザーが存在しない可能性があります").queue();
                            return;

                        }

                        User targetUser = option.getAsUser();

                        MessageEmbed embed = Util.decorateEmbedBuilder()
                                .setTitle(targetUser.getName())
                                .setImage(targetUser.getAvatarUrl() + "?size=1024")
                                .build();

                        event.getHook().sendMessageEmbeds(embed).queue();

                    }
                }
        );
    }
}
