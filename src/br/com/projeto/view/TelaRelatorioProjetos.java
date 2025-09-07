package br.com.projeto.view;

import br.com.projeto.model.Projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir o relatório de andamento dos projetos.
 */
public class TelaRelatorioProjetos extends JFrame {

    public TelaRelatorioProjetos(List<Projeto> projetos) {
        setTitle("Relatório - Andamento dos Projetos");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        setLocationRelativeTo(null); // Centraliza na tela

        // --- Criação da Tabela ---

        // Define as colunas da tabela
        String[] colunas = {"ID", "Nome do Projeto", "Status", "Data de Início", "Data de Fim", "Gerente"};

        // Cria o modelo da tabela, inicialmente sem linhas
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0);

        // Preenche o modelo com os dados dos projetos
        for (Projeto projeto : projetos) {
            Object[] rowData = {
                projeto.getId(),
                projeto.getNome(),
                projeto.getStatus(),
                projeto.getDataInicio(),
                projeto.getDataFim(),
                // Para o gerente, é melhor exibir o nome. Se o objeto gerente não estiver carregado, exibe o ID.
                // Isso pode ser melhorado no futuro para garantir que o nome do gerente sempre apareça.
                (projeto.getGerente() != null) ? projeto.getGerente().getNomeCompleto() : projeto.getGerenteId()
            };
            tableModel.addRow(rowData);
        }

        // Cria a JTable com o modelo preenchido
        JTable tabelaProjetos = new JTable(tableModel);
        tabelaProjetos.setEnabled(false); // Torna a tabela somente leitura
        tabelaProjetos.getTableHeader().setReorderingAllowed(false); // Impede a reordenação das colunas

        // Adiciona a tabela a um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        // Adiciona o painel de rolagem à janela
        add(scrollPane, BorderLayout.CENTER);
    }
}
