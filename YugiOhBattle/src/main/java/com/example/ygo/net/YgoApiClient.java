package com.example.ygo.net;

import com.example.ygo.model.Card;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class YgoApiClient {
    private static final String MONSTER_CARDS_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php?type=Normal%20Monster";
    private final HttpClient client;

    public YgoApiClient() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.ALWAYS) // 👈 importante por redirecciones
                .build();
    }

    public Card fetchRandomMonster() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MONSTER_CARDS_URL))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("HTTP error: " + resp.statusCode());
        }

        String body = resp.body();
        JSONObject json = new JSONObject(body);
        JSONArray data = json.getJSONArray("data");

        // elegir carta aleatoria del array
        JSONObject cardJson = data.getJSONObject((int) (Math.random() * data.length()));

        String id = String.valueOf(cardJson.optInt("id", cardJson.hashCode()));
        String name = cardJson.optString("name", "Unknown");
        int atk = cardJson.optInt("atk", 0);
        int def = cardJson.optInt("def", 0);

        String imageUrl = null;
        if (cardJson.has("card_images")) {
            JSONArray arr = cardJson.getJSONArray("card_images");
            if (arr.length() > 0) {
                JSONObject img = arr.getJSONObject(0);
                imageUrl = img.optString("image_url", null);
            }
        }
        return new Card(id, name, atk, def, imageUrl);
    }
}
