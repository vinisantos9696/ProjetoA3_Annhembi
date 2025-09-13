package br.com.projeto.view;

import br.com.projeto.dao.TarefaDAO;
import br.com.projeto.model.Tarefa;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela para visualização e gerenciamento de tarefas.
 */
public class TelaGerenciarTarefas extends JFrame {

    private final Usuario usuarioLogado;
    private JTable tabelaTarefas;
    private DefaultTableModel tableModel;
    private final TarefaDAO tarefaDAO;
    private List<Tarefa> tarefasAtuais;
    private TableRowSorter<DefaultTableModel> sorter;

    private final JButton btnNova;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JTextField txtFiltro;
    private final JComboBox<String> cmbColunaFiltro;

    public TelaGerenciarTarefas(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.tarefaDAO = new TarefaDAO();

        setTitle("Gerenciamento de Tarefas");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Superior (com botões e filtro) ---
        JPanel painelSuperior = new JPanel(new BorderLayout());

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNova = new JButton("Nova Tarefa");
        btnEditar = new JButton("Editar Tarefa");
        btnExcluir = new JButton("Excluir Tarefa");
        painelBotoes.add(btnNova);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelSuperior.add(painelBotoes, BorderLayout.NORTH);

        // --- Tabela de Tarefas (precisa ser criada antes do painel de filtro) ---
        String[] colunas = {"ID", "Título", "Status", "Projeto", "Responsável", "Início Previsto", "Fim Previsto", "Início Real", "Fim Real"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaTarefas = new JTable(tableModel);

        // Painel de Filtro
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
        painelSuperior.add(painelFiltro, BorderLayout.SOUTH);

        // Configuração final da tabela
        tabelaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        tabelaTarefas.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);

        // Adiciona os componentes à janela
        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões e Filtro ---
        btnNova.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getTarefaSelecionada()));
        btnExcluir.addActionListener(e -> excluirTarefa());

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

        // Carrega os dados iniciais e aplica as permissões
        carregarTarefas();
        configurarPermissoes();
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

    private void configurarPermissoes() {
        String perfil = usuarioLogado.getPerfil();
        boolean podeGerenciar = "administrador".equalsIgnoreCase(perfil) || "gerente".equalsIgnoreCase(perfil);

        btnNova.setEnabled(podeGerenciar);
        btnEditar.setEnabled(podeGerenciar);
        btnExcluir.setEnabled(podeGerenciar);
    }

    private void carregarTarefas() {
        tableModel.setRowCount(0);
        this.tarefasAtuais = tarefaDAO.buscarTodas();
        
        for (Tarefa tarefa : this.tarefasAtuais) {
            Object[] rowData = { 
                String.format("%03d", tarefa.getId()), // Formata o ID
                tarefa.getTitulo(), 
                tarefa.getStatus(),
                (tarefa.getProjeto() != null) ? tarefa.getProjeto().getNomeProjeto() : "N/A",
                (tarefa.getResponsavel() != null) ? tarefa.getResponsavel().getNomeCompleto() : "N/A",
                tarefa.getDataInicioPrevista(),
                tarefa.getDataFimPrevista(),
                tarefa.getDataInicioReal(),
                tarefa.getDataFimReal()
            };
            tableModel.addRow(rowData);
        }
    }

    private void abrirFormulario(Tarefa tarefa) {
        if (tarefa == null && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para editar.", "Nenhuma Tarefa Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TelaFormularioTarefa formulario = new TelaFormularioTarefa(this, tarefa);
        formulario.setVisible(true);

        if (formulario.isSalvo()) {
            carregarTarefas();
        }
    }

    private Tarefa getTarefaSelecionada() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tabelaTarefas.convertRowIndexToModel(selectedRow);
            String idFormatado = (String) tableModel.getValueAt(modelRow, 0);
            int tarefaId = Integer.parseInt(idFormatado); // Converte de volta para int
            return tarefasAtuais.stream().filter(t -> t.getId() == tarefaId).findFirst().orElse(null);
        }
        return null;
    }

    private void excluirTarefa() {
        Tarefa tarefa = getTarefaSelecionada();
        if (tarefa != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a tarefa: '" + tarefa.getTitulo() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                tarefaDAO.excluir(tarefa.getId());
                carregarTarefas();
                aplicarFiltro();
                JOptionPane.showMessageDialog(this, "Tarefa excluída com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para excluir.", "Nenhuma Tarefa Selecionada", JOptionPane.WARNING_MESSAGE);
        }
    }
}
