package net.runelite.client.live.inDevelopment.biggs.BMacro.discord;

import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DiscordWebhookSender {
    private static final Gson GSON = new Gson();
    private static final MediaType JSON = MediaType.parse("application/json");

    /**
     * Sends a Discord embed asynchronously with optional screenshot attachment.
     *
     * @param webhookUrl  The Discord webhook URL.
     * @param embedData   A map containing the embed's title, description, and color.
     * @param screenshot  Optional screenshot (BufferedImage) to attach. Pass null for no image.
     */
    public static void sendEmbedWithScreenshot(String webhookUrl, Map<String, String> embedData, BufferedImage screenshot, String imageUrl, String thumbnailUrl) {
        CompletableFuture.runAsync(() -> {
            try {
                MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                // Embed construction
                JSONObject embed = new JSONObject();
                embed.put("title", embedData.getOrDefault("title", "Biggs BH PK"));
                embed.put("description", embedData.getOrDefault("description", ""));
                embed.put("color", Integer.parseInt(embedData.getOrDefault("color", "3447003")));
                embed.put("timestamp", java.time.Instant.now().toString());

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    JSONObject imageObject = new JSONObject();
                    imageObject.put("url", imageUrl);
                    embed.put("image", imageObject);
                }

                if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                    JSONObject thumbObject = new JSONObject();
                    thumbObject.put("url", thumbnailUrl);
                    embed.put("thumbnail", thumbObject);
                }

                JSONObject payload = new JSONObject();
                payload.put("embeds", new JSONObject[]{embed});

                requestBodyBuilder.addFormDataPart(
                        "payload_json",
                        payload.toString()
                );

                if (screenshot != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(screenshot, "png", baos);
                    requestBodyBuilder.addFormDataPart(
                            "file",
                            "screenshot.png",
                            RequestBody.create(MediaType.parse("image/png"), baos.toByteArray())
                    );
                }

                sendAsyncRequest(webhookUrl, requestBodyBuilder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }





    /**
     * Sends a raw JSON embed to Discord asynchronously.
     *
     * @param webhookUrl The Discord webhook URL.
     * @param embedData  A map containing the embed's title, description, and color.
     */
    public static void sendEmbed(String webhookUrl, Map<String, String> embedData) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject embed = new JSONObject();
                embed.put("title", embedData.getOrDefault("title", "Polar Hunter Rumours"));
                embed.put("description", embedData.getOrDefault("description", ""));
                embed.put("color", Integer.parseInt(embedData.getOrDefault("color", "3447003")));

                JSONObject payload = new JSONObject();
                payload.put("embeds", new JSONObject[]{embed});

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(payload.toString().getBytes());
                    os.flush();
                }

                connection.getResponseCode(); // Trigger the request
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sends an asynchronous multipart request to the provided URL.
     *
     * @param webhookUrl The Discord webhook URL.
     * @param body       The multipart body to send.
     */
    private static void sendAsyncRequest(String webhookUrl, RequestBody body) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                response.close();
            }
        });
    }
}
