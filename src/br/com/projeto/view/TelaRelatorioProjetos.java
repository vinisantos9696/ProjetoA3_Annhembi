package br.com.projeto.view;

import br.com.projeto.model.Projeto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir o relatório de andamento dos projetos.
 */
public class TelaRelatorioProjetos extends JFrame {

    public TelaRelatorioProjetos(List<Projeto> projetos) {
        setTitle("Relatório - Andamento dos Projetos");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Principal com Margem ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Adiciona margem de 10px

        // --- Criação da Tabela ---
        String[] colunas = {"ID", "Nome do Projeto", "Status", "Data de Início", "Data Prev. Fim", "Gerente"};

        // Cria o modelo da tabela, tornando as células não editáveis
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição de qualquer célula
            }
        };

        // Preenche o modelo com os dados dos projetos
        for (Projeto projeto : projetos) {
            Object[] rowData = {
                projeto.getId(),
                projeto.getNomeProjeto(),
                projeto.getStatus(),
                projeto.getDataInicio(),
                projeto.getDataFimPrevista(),
                (projeto.getGerente() != null) ? projeto.getGerente().getNomeCompleto() : projeto.getIdGerente()
            };
            tableModel.addRow(rowData);
        }

        // Cria a JTable com o modelo preenchido
        JTable tabelaProjetos = new JTable(tableModel);
        tabelaProjetos.setFillsViewportHeight(true); // Garante que a tabela preencha a altura da viewport
        tabelaProjetos.getTableHeader().setReorderingAllowed(false); // Impede a reordenação das colunas
        tabelaProjetos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Deixa o cabeçalho em negrito
        tabelaProjetos.setRowHeight(25); // Aumenta a altura da linha para melhor leitura

        // --- Ajuste de Largura das Colunas ---
        TableColumnModel columnModel = tabelaProjetos.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID
        columnModel.getColumn(0).setMaxWidth(80);
        columnModel.getColumn(1).setPreferredWidth(300); // Nome do Projeto
        columnModel.getColumn(2).setPreferredWidth(100); // Status
        columnModel.getColumn(3).setPreferredWidth(100); // Data de Início
        columnModel.getColumn(4).setPreferredWidth(100); // Data Prev. Fim
        columnModel.getColumn(5).setPreferredWidth(150); // Gerente

        // Adiciona a tabela a um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        // Adiciona o painel de rolagem ao painel principal
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Adiciona o painel principal à janela
        add(mainPanel);
    }
}
