package net.ramuremo.feature;

import com.google.gson.Gson;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.ramuremo.internal.CommandConsumer;
import net.ramuremo.internal.Feature;
import net.ramuremo.internal.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class AstolfoFeature implements Feature {

    private static ImageModel fetch() throws Exception {
        URL url = new URL("https://astolfo.rocks/api/images/random");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            return gson.fromJson(response.toString(), ImageModel.class);
        } else {
            throw new RuntimeException("画像取得中にエラーが発生しました");
        }
    }

    @Override
    public Set<CommandConsumer> getCommands() {
        return Set.of(
                new CommandConsumer(new CommandDataImpl("astolfo", "アストルフォの画像を拾ってきます")) {
                    @Override
                    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
                        ImageModel result = fetch();
                        event.getHook().editOriginalEmbeds(
                                Util.decorateEmbedBuilder()
                                        .setDescription("ソース: " + result.getSource())
                                        .setImage("https://astolfo.rocks/astolfo/" + result.getId() + "." + result.getFile_extension())
                                        .build()
                        ).complete();
                    }
                }
        );
    }

    private static class ImageModel {
        private long id, views, file_size, width, height;
        private String rating, created_at, updated_at, source, file_extension, mimetype;

        public long getId() {
            return id;
        }

        public long getViews() {
            return views;
        }

        public long getFile_size() {
            return file_size;
        }

        public long getWidth() {
            return width;
        }

        public long getHeight() {
            return height;
        }

        public String getRating() {
            return rating;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getSource() {
            return source;
        }

        public String getFile_extension() {
            return file_extension;
        }

        public String getMimetype() {
            return mimetype;
        }
    }
}
