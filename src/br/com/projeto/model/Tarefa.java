package br.com.projeto.model;

import java.util.Date;

public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private String status; // Ex: "A fazer", "Em andamento", "Concluída"
    private Date dataInicioPrevista;
    private Date dataFimPrevista;
    private Date dataInicioReal;
    private Date dataFimReal;
    private Projeto projeto; // Projeto ao qual a tarefa pertence
    private Usuario responsavel; // Usuário responsável pela tarefa

    // Construtor Padrão
    public Tarefa() {
    }

    // Construtor com todos os campos
    public Tarefa(int id, String titulo, String descricao, String status, Date dataInicioPrevista, Date dataFimPrevista, Date dataInicioReal, Date dataFimReal, Projeto projeto, Usuario responsavel) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.dataInicioPrevista = dataInicioPrevista;
        this.dataFimPrevista = dataFimPrevista;
        this.dataInicioReal = dataInicioReal;
        this.dataFimReal = dataFimReal;
        this.projeto = projeto;
        this.responsavel = responsavel;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataInicioPrevista() {
        return dataInicioPrevista;
    }

    public void setDataInicioPrevista(Date dataInicioPrevista) {
        this.dataInicioPrevista = dataInicioPrevista;
    }

    public Date getDataFimPrevista() {
        return dataFimPrevista;
    }

    public void setDataFimPrevista(Date dataFimPrevista) {
        this.dataFimPrevista = dataFimPrevista;
    }

    public Date getDataInicioReal() {
        return dataInicioReal;
    }

    public void setDataInicioReal(Date dataInicioReal) {
        this.dataInicioReal = dataInicioReal;
    }

    public Date getDataFimReal() {
        return dataFimReal;
    }

    public void setDataFimReal(Date dataFimReal) {
        this.dataFimReal = dataFimReal;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }
}
