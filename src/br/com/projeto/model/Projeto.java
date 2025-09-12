package br.com.projeto.model;

import java.util.Date;
import java.util.Objects;

public class Projeto {

    private int id;
    private String nomeProjeto;
    private String descricao;
    private Date dataInicio;
    private Date dataFimPrevista;
    private String status; // Ex: "Em andamento", "Concluído", "Pendente"
    private int idGerente; // ID do gerente, para facilitar o acesso ao banco
    private Usuario gerente; // Referência ao objeto gerente completo

    // Construtor Padrão
    public Projeto() {
    }

    // Construtor com todos os campos
    public Projeto(int id, String nomeProjeto, String descricao, Date dataInicio, Date dataFimPrevista, String status, Usuario gerente) {
        this.id = id;
        this.nomeProjeto = nomeProjeto;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.status = status;
        this.gerente = gerente;
        if (gerente != null) {
            this.idGerente = gerente.getId();
        }
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFimPrevista() {
        return dataFimPrevista;
    }

    public void setDataFimPrevista(Date dataFimPrevista) {
        this.dataFimPrevista = dataFimPrevista;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdGerente() {
        return idGerente;
    }

    public void setIdGerente(int idGerente) {
        this.idGerente = idGerente;
    }

    public Usuario getGerente() {
        return gerente;
    }

    public void setGerente(Usuario gerente) {
        this.gerente = gerente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projeto projeto = (Projeto) o;
        return id == projeto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
