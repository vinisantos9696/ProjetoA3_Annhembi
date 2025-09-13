package br.com.projeto.view;

import br.com.projeto.model.Projeto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir o relatório de andamento dos projetos.
 */
public class TelaRelatorioProjetos extends JFrame {

    private JTable tabelaProjetos;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtFiltro;
    private JComboBox<String> cmbColunaFiltro;

    public TelaRelatorioProjetos(List<Projeto> projetos) {
        setTitle("Relatório - Andamento dos Projetos");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Principal com Margem ---
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10)); // Adiciona espaçamento vertical
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Criação da Tabela (precisa ser criada antes do painel de filtro) ---
        String[] colunas = {"ID", "Nome do Projeto", "Status", "Data de Início", "Data Prev. Fim", "Gerente"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProjetos = new JTable(tableModel);

        // --- Painel de Filtro ---
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltro.add(new JLabel("Filtrar por:"));
        cmbColunaFiltro = new JComboBox<>();
        cmbColunaFiltro.addItem("Todas as Colunas");
        for (String coluna : colunas) {
            cmbColunaFiltro.addItem(coluna);
        }
        painelFiltro.add(cmbColunaFiltro);
        painelFiltro.add(new JLabel("Valor:"));
        txtFiltro = new JTextField(30);
        painelFiltro.add(txtFiltro);
        mainPanel.add(painelFiltro, BorderLayout.NORTH);

        // Preenche o modelo com os dados dos projetos
        for (Projeto projeto : projetos) {
            Object[] rowData = {
                projeto.getId(),
                projeto.getNomeProjeto(),
                projeto.getStatus(),
                projeto.getDataInicio(),
                projeto.getDataFimPrevista(),
                (projeto.getGerente() != null) ? projeto.getGerente().getNomeCompleto() : "N/A"
            };
            tableModel.addRow(rowData);
        }

        // Configuração final da tabela
        tabelaProjetos.setFillsViewportHeight(true);
        tabelaProjetos.getTableHeader().setReorderingAllowed(false);
        tabelaProjetos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaProjetos.setRowHeight(25);

        sorter = new TableRowSorter<>(tableModel);
        tabelaProjetos.setRowSorter(sorter);

        // --- Ajuste de Largura das Colunas ---
        TableColumnModel columnModel = tabelaProjetos.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID
        columnModel.getColumn(0).setMaxWidth(80);
        columnModel.getColumn(1).setPreferredWidth(300); // Nome do Projeto
        columnModel.getColumn(2).setPreferredWidth(100); // Status
        columnModel.getColumn(3).setPreferredWidth(100); // Data de Início
        columnModel.getColumn(4).setPreferredWidth(100); // Data Prev. Fim
        columnModel.getColumn(5).setPreferredWidth(150); // Gerente

        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // --- Ações do Filtro ---
        DocumentListener listenerFiltro = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override
            public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override
            public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        };
        txtFiltro.getDocument().addDocumentListener(listenerFiltro);
        cmbColunaFiltro.addActionListener(e -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String texto = txtFiltro.getText();
        String colunaSelecionada = (String) cmbColunaFiltro.getSelectedItem();

        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            int indiceColuna = -1;
            if (colunaSelecionada != null && !"Todas as Colunas".equals(colunaSelecionada)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (tableModel.getColumnName(i).equals(colunaSelecionada)) {
                        indiceColuna = i;
                        break;
                    }
                }
            }

            if (indiceColuna != -1) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, indiceColuna));
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
            }
        }
    }
}
