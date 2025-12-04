package com.example.afmobile;


public class Medicamento {
    private String id;
    private String nome;
    private String descricao;
    private String horario;
    private boolean consumido;
    private String pokemonUrl;

    // NOVO CAMPO:
    private String pokemonNome;

    // Construtor Vazio (Obrigatório para o Firebase)
    public Medicamento() {}

    // Construtor Completo (Atualizado com pokemonNome)
    public Medicamento(String nome, String descricao, String horario, String pokemonUrl, String pokemonNome) {
        this.nome = nome;
        this.descricao = descricao;
        this.horario = horario;
        this.consumido = false;
        this.pokemonUrl = pokemonUrl;
        this.pokemonNome = pokemonNome; // <--- Salva o nome aqui
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public boolean isConsumido() { return consumido; }
    public void setConsumido(boolean consumido) { this.consumido = consumido; }

    public String getPokemonUrl() { return pokemonUrl; }
    public void setPokemonUrl(String pokemonUrl) { this.pokemonUrl = pokemonUrl; }

    // Getter e Setter do Novo Campo
    public String getPokemonNome() { return pokemonNome; }
    public void setPokemonNome(String pokemonNome) { this.pokemonNome = pokemonNome; }
}