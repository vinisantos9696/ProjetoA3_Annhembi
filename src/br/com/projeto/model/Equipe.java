package br.com.projeto.model;

import java.util.List;

public class Equipe {

    private int idEquipe;
    private String nomeEquipe;
    private String descricao;
    private List<Usuario> membros;
    private List<Projeto> projetosAlocados;

    // Construtor Padrão
    public Equipe() {
    }

    // Construtor com campos principais
    public Equipe(int idEquipe, String nomeEquipe, String descricao) {
        this.idEquipe = idEquipe;
        this.nomeEquipe = nomeEquipe;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getIdEquipe() {
        return idEquipe;
    }

    public void setIdEquipe(int idEquipe) {
        this.idEquipe = idEquipe;
    }

    public String getNomeEquipe() {
        return nomeEquipe;
    }

    public void setNomeEquipe(String nomeEquipe) {
        this.nomeEquipe = nomeEquipe;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
