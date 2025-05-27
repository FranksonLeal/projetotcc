package com.example.educapoio;

public class recomendado {
    private String id;
    private String titulo;
    private String dataPublicacao;

    // Construtor vazio obrigatório para o Firestore
    public recomendado() { }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }
    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
}
