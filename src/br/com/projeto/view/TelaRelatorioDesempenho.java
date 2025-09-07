package br.com.projeto.view;

import br.com.projeto.model.DesempenhoColaboradorDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir o relatório de desempenho dos colaboradores.
 */
public class TelaRelatorioDesempenho extends JFrame {

    public TelaRelatorioDesempenho(List<DesempenhoColaboradorDTO> relatorio) {
        setTitle("Relatório - Desempenho dos Colaboradores");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        setLocationRelativeTo(null); // Centraliza na tela

        // --- Criação da Tabela ---

        // Define as colunas da tabela
        String[] colunas = {"Colaborador", "Total de Tarefas", "Tarefas Concluídas", "Tarefas em Andamento"};

        // Cria o modelo da tabela, inicialmente sem linhas
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0);

        // Preenche o modelo com os dados do relatório
        for (DesempenhoColaboradorDTO dto : relatorio) {
            Object[] rowData = {
                dto.getNomeColaborador(),
                dto.getTotalTarefas(),
                dto.getTarefasConcluidas(),
                dto.getTarefasEmAndamento()
            };
            tableModel.addRow(rowData);
        }

        // Cria a JTable com o modelo preenchido
        JTable tabelaDesempenho = new JTable(tableModel);
        tabelaDesempenho.setEnabled(false); // Torna a tabela somente leitura
        tabelaDesempenho.getTableHeader().setReorderingAllowed(false); // Impede a reordenação das colunas

        // Adiciona a tabela a um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(tabelaDesempenho);

        // Adiciona o painel de rolagem à janela
        add(scrollPane, BorderLayout.CENTER);
    }
}
