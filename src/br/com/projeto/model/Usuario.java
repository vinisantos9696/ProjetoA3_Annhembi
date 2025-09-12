package br.com.projeto.model;

import java.util.Objects;

public class Usuario {

    private int id;
    private String nomeCompleto;
    private String login;
    private String senha;
    private String perfil; // "administrador", "gerente", "colaborador"

    // Construtor Padrão
    public Usuario() {
    }

    // Construtor com todos os campos
    public Usuario(int id, String nomeCompleto, String login, String senha, String perfil) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
