package br.com.projeto.view;

import br.com.projeto.model.DesempenhoColaboradorDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir o relatório de desempenho dos colaboradores.
 */
public class TelaRelatorioDesempenho extends JFrame {

    private JTable tabelaDesempenho;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtFiltro;
    private JComboBox<String> cmbColunaFiltro;

    public TelaRelatorioDesempenho(List<DesempenhoColaboradorDTO> relatorio) {
        setTitle("Relatório - Desempenho dos Colaboradores");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Principal com Margem ---
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Criação da Tabela (precisa ser criada antes do painel de filtro) ---
        String[] colunas = {"Colaborador", "Total de Tarefas", "Tarefas Concluídas", "Tarefas em Andamento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição de qualquer célula
            }
        };
        tabelaDesempenho = new JTable(tableModel);

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

        // Configuração final da tabela
        tabelaDesempenho.getTableHeader().setReorderingAllowed(false);
        sorter = new TableRowSorter<>(tableModel);
        tabelaDesempenho.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabelaDesempenho);
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
