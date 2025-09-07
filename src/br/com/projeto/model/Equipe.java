package br.com.projeto.model;

import java.util.List;

public class Equipe {

    private int id;
    private String nome;
    private List<Usuario> membros;
    private List<Projeto> projetosAlocados;

    // Construtor Padrão
    public Equipe() {
    }

    // Construtor com todos os campos
    public Equipe(int id, String nome, List<Usuario> membros, List<Projeto> projetosAlocados) {
        this.id = id;
        this.nome = nome;
        this.membros = membros;
        this.projetosAlocados = projetosAlocados;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }

    public List<Projeto> getProjetosAlocados() {
        return projetosAlocados;
    }

    public void setProjetosAlocados(List<Projeto> projetosAlocados) {
        this.projetosAlocados = projetosAlocados;
    }
}
