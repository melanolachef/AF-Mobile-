package com.example.afmobile;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PokemonService {

    // Interface atualizada para receber URL e NOME
    public interface PokemonCallback {
        void onResultado(String urlImagem, String nomePokemon);
    }

    public static void sortearPokemon(PokemonCallback callback) {
        new Thread(() -> {
            try {
                // Sorteia entre 1 e 150 (Geração 1)
                int id = (int) (Math.random() * 150) + 1;
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader leitor = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String linha;
                    while ((linha = leitor.readLine()) != null) {
                        jsonBuilder.append(linha);
                    }
                    leitor.close();

                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(jsonBuilder.toString(), JsonObject.class);

                    // Pega a imagem
                    String imgUrl = json.getAsJsonObject("sprites")
                            .get("front_default").getAsString();

                    // Pega o nome e deixa a primeira letra maiúscula
                    String nomeRaw = json.get("name").getAsString();
                    String nomeFormatado = nomeRaw.substring(0, 1).toUpperCase() + nomeRaw.substring(1);

                    // Devolve os dois dados
                    callback.onResultado(imgUrl, nomeFormatado);
                } else {
                    callback.onResultado(null, "Desconhecido");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResultado(null, "Erro");
            }
        }).start();
    }
}

