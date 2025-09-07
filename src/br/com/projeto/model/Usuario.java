package br.com.projeto.model;

public class Usuario {

    private int id;
    private String nomeCompleto;
    private String username;
    private String senha;
    private String perfil; // "administrador", "gerente", "colaborador"

    // Construtor Padrão
    public Usuario() {
    }

    // Construtor com todos os campos
    public Usuario(int id, String nomeCompleto, String username, String senha, String perfil) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
