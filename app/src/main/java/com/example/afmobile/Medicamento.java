package com.example.afmobile;

public class Medicamento {
    private String id; // ID do documento no Firestore
    private String nome;
    private String descricao;
    private String horario;
    private boolean consumido;
    private String pokemonUrl; // URL da imagem

    // Construtor vazio obrigatório para o Firestore [cite: 653]
    public Medicamento() {}

    public Medicamento(String nome, String descricao, String horario, String pokemonUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.horario = horario;
        this.consumido = false;
        this.pokemonUrl = pokemonUrl;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public String getHorario() { return horario; }
    public boolean isConsumido() { return consumido; }
    public void setConsumido(boolean consumido) { this.consumido = consumido; }
    public String getPokemonUrl() { return pokemonUrl; }
}