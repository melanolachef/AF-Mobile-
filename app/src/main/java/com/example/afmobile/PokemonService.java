package com.example.afmobile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PokemonService {

    public interface PokemonCallback {
        void onResultado(String urlImagem);
    }

    public static void sortearPokemon(PokemonCallback callback) {
        new Thread(() -> { // Thread nativa (Aula 10 e 11) [cite: 1024, 821]
            try {
                // Sorteia ID entre 1 e 150
                int id = (int) (Math.random() * 150) + 1;
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // [cite: 825]
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader leitor = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String linha;
                    while ((linha = leitor.readLine()) != null) {
                        jsonBuilder.append(linha);
                    }
                    leitor.close();

                    // Parse com Gson (Aula 11) [cite: 844]
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(jsonBuilder.toString(), JsonObject.class);

                    // Navegar no JSON para achar a imagem (sprites -> front_default)
                    String imgUrl = json.getAsJsonObject("sprites")
                            .get("front_default").getAsString();

                    callback.onResultado(imgUrl);
                } else {
                    callback.onResultado(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResultado(null);
            }
        }).start();
    }
}
