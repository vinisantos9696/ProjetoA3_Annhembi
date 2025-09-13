package br.com.projeto.view;

import br.com.projeto.dao.EquipeDAO;
import br.com.projeto.model.Equipe;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela para visualização e gerenciamento de equipes.
 */
public class TelaGerenciarEquipes extends JFrame {

    private final Usuario usuarioLogado;
    private JTable tabelaEquipes;
    private DefaultTableModel tableModel;
    private final EquipeDAO equipeDAO;
    private List<Equipe> equipesAtuais;
    private TableRowSorter<DefaultTableModel> sorter;

    private final JButton btnNova;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JTextField txtFiltro;
    private final JComboBox<String> cmbColunaFiltro;

    public TelaGerenciarEquipes(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.equipeDAO = new EquipeDAO();

        setTitle("Gerenciamento de Equipes");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Superior (com botões e filtro) ---
        JPanel painelSuperior = new JPanel(new BorderLayout());

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNova = new JButton("Nova Equipe");
        btnEditar = new JButton("Editar Equipe");
        btnExcluir = new JButton("Excluir Equipe");
        painelBotoes.add(btnNova);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelSuperior.add(painelBotoes, BorderLayout.NORTH);

        // --- Tabela de Equipes (precisa ser criada antes do painel de filtro) ---
        String[] colunas = {"ID", "Nome da Equipe", "Descrição"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEquipes = new JTable(tableModel);

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
        tabelaEquipes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        tabelaEquipes.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);

        // Adiciona os componentes à janela
        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões e Filtro ---
        btnNova.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getEquipeSelecionada()));
        btnExcluir.addActionListener(e -> excluirEquipe());

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
        carregarEquipes();
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

    private void carregarEquipes() {
        tableModel.setRowCount(0);
        this.equipesAtuais = equipeDAO.buscarTodas();
        
        for (Equipe equipe : this.equipesAtuais) {
            Object[] rowData = { 
                String.format("%03d", equipe.getIdEquipe()), // Formata o ID
                equipe.getNomeEquipe(), 
                equipe.getDescricao() 
            };
            tableModel.addRow(rowData);
        }
    }

    private void abrirFormulario(Equipe equipe) {
        if (equipe == null && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma equipe para editar.", "Nenhuma Equipe Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TelaFormularioEquipe formulario = new TelaFormularioEquipe(this, equipe);
        formulario.setVisible(true);

        if (formulario.isSalvo()) {
            carregarEquipes();
        }
    }

    private Equipe getEquipeSelecionada() {
        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tabelaEquipes.convertRowIndexToModel(selectedRow);
            String idFormatado = (String) tableModel.getValueAt(modelRow, 0);
            int equipeId = Integer.parseInt(idFormatado); // Converte de volta para int
            // Usa o método buscarPorId para carregar o objeto completo com membros e projetos
            return equipeDAO.buscarPorId(equipeId);
        }
        return null;
    }

    private void excluirEquipe() {
        Equipe equipe = getEquipeSelecionada();
        if (equipe != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a equipe '" + equipe.getNomeEquipe() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                equipeDAO.excluir(equipe.getIdEquipe());
                carregarEquipes();
                aplicarFiltro();
                JOptionPane.showMessageDialog(this, "Equipe excluída com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma equipe para excluir.", "Nenhuma Equipe Selecionada", JOptionPane.WARNING_MESSAGE);
        }
    }
}
