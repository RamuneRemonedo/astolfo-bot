package net.ramuremo.feature;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.ramuremo.internal.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class NotifyBumpFeature implements Feature {

    private static void schedule(long channelId, long userId, BumpType type) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            try {
                TextChannel channel = Client.getClient().getTextChannelById(channelId);
                if (channel == null) return;
                channel.sendMessage("<@" + userId + ">さん!! " + type.toCommand() + "でアップできるようになりました!!").queue();
            } catch (Exception ignored) {
            }
        }, type.getCooldown(), TimeUnit.SECONDS);
    }

    @Override
    public Set<CommandConsumer> getCommands() {
        return Set.of(
                new CommandConsumer(new CommandDataImpl("bump-notify", "Bump通知をお知らせします")
                        .setGuildOnly(true)) {
                    @Override
                    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
                        Member member = event.getMember();
                        if (member == null) throw new RuntimeException("メンバーがnullです");
                        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                            event.getHook().editOriginal("このコマンドは管理者のみが実行できます").queue();
                            return;
                        }
                        MongoCollection<Document> collection = DBHandler.getDatabase().getCollection("bump-notify");
                        long guildId = event.getMember().getGuild().getIdLong();
                        Bson filter = Filters.eq("id", guildId);
                        Document result = collection.find(filter).first();
                        boolean enabled = result != null;

                        if (enabled) {
                            collection.deleteOne(result);
                            event.getHook().editOriginalEmbeds(
                                    Util.decorateEmbedBuilder()
                                            .setTitle("Bump通知が無効になりました!")
                                            .setDescription("再び有効にするには/bump-notifyコマンドを打ってください。")
                                            .build()
                            ).queue();
                        } else {
                            collection.insertOne(new Document().append("id", guildId));
                            event.getHook().editOriginalEmbeds(
                                    Util.decorateEmbedBuilder()
                                            .setTitle("Bump通知が有効にになりました!")
                                            .setDescription("無効にするには/bump-notifyコマンドを打ってください。")
                                            .build()
                            ).queue();
                        }
                    }
                }
        );
    }

    @SubscribeEvent
    public void onMessageReceived(MessageUpdateEvent event) throws Exception {
        if (!event.getAuthor().isBot()) return;

        long authorId = event.getAuthor().getIdLong();
        BumpType type = Arrays.stream(BumpType.values())
                .filter(bumpType -> bumpType.getBotId() == authorId)
                .findFirst()
                .orElse(null);

        if (type == null) return;

        MongoCollection<Document> collection = DBHandler.getDatabase().getCollection("bump-notify");
        Bson filter = Filters.eq("id", event.getGuild().getIdLong());
        boolean enabled = collection.find(filter).first() != null;

        if (!enabled) return;

        boolean success = type.checker.apply(event.getMessage());

        if (!success) return;

        event.getChannel().sendMessage(
                event.getAuthor().getAsMention() + "さんがアップしたよ!!!\n" +
                        "次のアップは<t:" + (Util.getCurrentUnixTime() + type.getCooldown()) + ":R>だよ!"
        ).queue();
        schedule(event.getChannel().getIdLong(), Objects.requireNonNull(event.getMessage().getInteraction()).getUser().getIdLong(), type);
    }

    enum BumpType {
        DISBOARD(302050872383242240L, "bump", 947088344167366698L, 7200L, (message ->
                message.getEmbeds().stream().anyMatch(embed -> {
                    String description = embed.getDescription();
                    if (description == null) return false;
                    return description.contains("アップしたよ");
                })

        )),
        DISSOKU(761562078095867916L, "dissoku up", 828002256690610256L, 3600L, (message ->
                message.getEmbeds().stream().anyMatch(embed ->
                        embed.getFields().stream().anyMatch(field -> {
                            String name = field.getName();
                            if (name == null) return false;
                            return name.contains("アップしたよ");
                        })
                )
        ));

        private final long botId, commandId, cooldown;
        private final String commandName;
        private final Function<Message, Boolean> checker;

        BumpType(long botId, String commandName, long commandId, long cooldown, Function<Message, Boolean> checker) {
            this.botId = botId;
            this.commandName = commandName;
            this.commandId = commandId;
            this.cooldown = cooldown;
            this.checker = checker;
        }

        public long getBotId() {
            return botId;
        }

        public String getCommandName() {
            return commandName;
        }

        public long getCommandId() {
            return commandId;
        }

        public long getCooldown() {
            return cooldown;
        }

        public Function<Message, Boolean> getChecker() {
            return checker;
        }

        public String toCommand() {
            return "</" + commandName + ":" + commandId + ">";
        }
    }
}
