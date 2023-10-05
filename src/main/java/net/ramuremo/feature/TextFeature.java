package net.ramuremo.feature;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.ramuremo.internal.CommandConsumer;
import net.ramuremo.internal.Feature;
import net.ramuremo.internal.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TextFeature implements Feature {

    private static final Font FONT;

    static {
        try {
            FONT = Font.createFont(Font.TRUETYPE_FONT, new File("ranobe-pop.otf")).deriveFont(Font.BOLD, 70);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<CommandConsumer> getCommands() {
        return Set.of(
                new CommandConsumer(new CommandDataImpl("text", "テキストを可愛い画像にします")
                        .addOptions(
                                new OptionData(OptionType.STRING, "text", "生成するテキストを入力してください", true),
                                new OptionData(OptionType.STRING, "color", "生成する文字色を選択してください", true)
                                        .addChoices(Util.toChoices(TextColor.getStrings()))
                        )) {
                    @Override
                    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
                        final OptionMapping textOption = event.getOption("text");

                        if (textOption == null) {
                            event.getHook().editOriginal("エラー: 引数の文字が指定されていません。").queue();
                            return;
                        }

                        final String text = textOption.getAsString();

                        if (text.length() >= 80) {
                            event.getHook().editOriginal("エラー: 引数の文字は80文字未満にしてください。").queue();
                            return;
                        }

                        final OptionMapping colorOption = event.getOption("color");

                        if (colorOption == null) {
                            event.getHook().editOriginal("エラー: 引数のカラーが指定されていません。").queue();
                            return;
                        }

                        final String color = colorOption.getAsString();

                        final InputStream imageInputStream = generate(TextColor.parseColor(color), text);

                        event.getHook().sendFiles(FileUpload.fromData(imageInputStream, "text.png")).queue();

                    }
                }
        );
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot() || author.isSystem()) return;
        String content = event.getMessage().getContentRaw();
        if (!content.startsWith("$")) return;
        String text = content.substring(1);
        try {
            event.getMessage().delete().queue();
        } catch (Exception ignored) {
        }
        try {
            event.getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">")
                    .addFiles(FileUpload.fromData(generate(Util.random(TextColor.values()), text), "text.png"))
                    .setAllowedMentions(null)
                    .queue();
        } catch (Exception ignored) {
        }
    }

    private InputStream generate(TextColor outlineColor, String text) throws Exception {
        final Color textColor = Color.WHITE;
        final int outlineWidth = 5;

        // テキストのサイズを取得する
        final FontRenderContext frc = new FontRenderContext(null, true, true);
        final GlyphVector gv = FONT.createGlyphVector(frc, text);
        final int textWidth = (int) gv.getVisualBounds().getWidth();
        final int textHeight = (int) gv.getVisualBounds().getHeight();

        // BufferedImageオブジェクトを作成する
        final int imageWidth = textWidth + outlineWidth * 2 + 30;
        final int imageHeight = textHeight + outlineWidth * 2 + 30;
        final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // Graphics2Dオブジェクトを取得する
        final Graphics2D g2d = image.createGraphics();

        // アンチエイリアスを有効にする
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 縁取りされたテキストを描画する
        g2d.setFont(FONT);
        g2d.setColor(outlineColor.getColor());
        for (int x = -outlineWidth; x <= outlineWidth; x++) {
            for (int y = -outlineWidth; y <= outlineWidth; y++) {
                if (x != 0 || y != 0) {
                    g2d.drawString(text, outlineWidth + x + 4, imageHeight - outlineWidth * 2 + y - 4);
                }
            }
        }
        g2d.setColor(textColor);
        g2d.drawString(text, outlineWidth + 4, imageHeight - outlineWidth * 2 - 4);

        // Graphics2Dオブジェクトを解放する
        g2d.dispose();

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);

        return new ByteArrayInputStream(os.toByteArray());
    }

    enum TextColor {
        PURPLE(new Color(0xBC74FF)),
        GREEN (new Color(0x49B600)),
        BLUE  (new Color(0x6A89FF)),
        AQUA  (new Color(0x00B5FF)),
        PINK  (new Color(0xFF53D1)),
        RED   (new Color(0xFF4F4F)),
        YELLOW(new Color(0xFFCD07));

        private final Color color;

        TextColor(Color color) {
            this.color = color;
        }

        public static String[] getStrings() {
            final List<String> strings = new ArrayList<>();
            for (TextColor color : values()) {
                strings.add(color.name().toLowerCase());
            }
            return strings.toArray(new String[0]);
        }

        public static TextColor parseColor(String s) {
            for (TextColor color : values()) {
                if (color.name().toLowerCase().equals(s)) return color;
            }
            return PURPLE;
        }

        public Color getColor() {
            return color;
        }
    }
}
