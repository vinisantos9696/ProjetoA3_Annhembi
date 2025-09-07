package br.com.projeto.model;

/**
 * DTO (Data Transfer Object) para carregar os dados agregados do relatório de desempenho.
 * Esta classe não representa uma tabela do banco, mas sim o resultado de uma consulta.
 */
public class DesempenhoColaboradorDTO {

    private String nomeColaborador;
    private int totalTarefas;
    private int tarefasConcluidas;
    private int tarefasEmAndamento;

    // Getters e Setters

    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public void setNomeColaborador(String nomeColaborador) {
        this.nomeColaborador = nomeColaborador;
    }

    public int getTotalTarefas() {
        return totalTarefas;
    }

    public void setTotalTarefas(int totalTarefas) {
        this.totalTarefas = totalTarefas;
    }

    public int getTarefasConcluidas() {
        return tarefasConcluidas;
    }

    public void setTarefasConcluidas(int tarefasConcluidas) {
        this.tarefasConcluidas = tarefasConcluidas;
    }

    public int getTarefasEmAndamento() {
        return tarefasEmAndamento;
    }

    public void setTarefasEmAndamento(int tarefasEmAndamento) {
        this.tarefasEmAndamento = tarefasEmAndamento;
    }
}
