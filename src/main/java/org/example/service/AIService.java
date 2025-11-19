package org.example.service;

import okhttp3.*;
import org.json.JSONObject;

public class AIService {

    private  final  String apiKey ;
    private static final OkHttpClient client = new OkHttpClient();

    public AIService(String apiKey) {
        this.apiKey = apiKey;
    }

    public  String generateDescription(String title) {

        try {
            String url =
                    "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key="
                            + apiKey;
            // phải nhớ thử lại liên tục, rất có thể gemini sẽ bị overload 503: không ổn định lắm, nhưng đã test qua postman
            //nhớ phải tạo env trước khi up lên git?
            //có thể thử dùng openAI?????? kiểm tra lại xem cloudflare còn sập không đã

            JSONObject bodyJson = new JSONObject()
                    .put("contents", new org.json.JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new org.json.JSONArray()
                                            .put(new JSONObject()
                                                    .put("text", "Write a short task description for: " + title)
                                            )
                                    )
                            )
                    );

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String raw = response.body().string();

            System.out.println(" RAW API RESPONSE");
            System.out.println(raw);
            System.out.println("HTTP CODE = " + response.code());

            JSONObject json = new JSONObject(raw);

            if (json.has("error")) {
                return "AI Error: " + json.getJSONObject("error").getString("message");
            }

            String text =
                    json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

            return text;

        } catch (Exception e) {
            e.printStackTrace();
            return "AI Error: " + e.getMessage();
        }
    }
}
