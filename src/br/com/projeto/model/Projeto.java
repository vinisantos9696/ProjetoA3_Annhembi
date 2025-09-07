package br.com.projeto.model;

import java.util.Date;

public class Projeto {

    private int id;
    private String nome;
    private String descricao;
    private Date dataInicio;
    private Date dataFim;
    private String status; // Ex: "Em andamento", "Concluído", "Pendente"
    private int gerenteId; // ID do gerente, para facilitar o acesso ao banco
    private Usuario gerente; // Referência ao objeto gerente completo

    // Construtor Padrão
    public Projeto() {
    }

    // Construtor com todos os campos
    public Projeto(int id, String nome, String descricao, Date dataInicio, Date dataFim, String status, Usuario gerente) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.gerente = gerente;
        if (gerente != null) {
            this.gerenteId = gerente.getId();
        }
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

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(int gerenteId) {
        this.gerenteId = gerenteId;
    }

    public Usuario getGerente() {
        return gerente;
    }

    public void setGerente(Usuario gerente) {
        this.gerente = gerente;
    }
}
