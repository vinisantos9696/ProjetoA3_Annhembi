package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela principal para visualização e gerenciamento de projetos.
 * As permissões de edição são controladas com base no perfil do usuário.
 */
public class TelaGerenciarProjetos extends JFrame {

    private final Usuario usuarioLogado;
    private JTable tabelaProjetos;
    private DefaultTableModel tableModel;
    private final ProjetoDAO projetoDAO;
    private List<Projeto> projetosAtuais;
    private TableRowSorter<DefaultTableModel> sorter;

    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JTextField txtFiltro;
    private final JComboBox<String> cmbColunaFiltro;

    public TelaGerenciarProjetos(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.projetoDAO = new ProjetoDAO();

        setTitle("Gerenciamento de Projetos");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Superior (com botões e filtro) ---
        JPanel painelSuperior = new JPanel(new BorderLayout());

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo Projeto");
        btnEditar = new JButton("Editar Projeto");
        btnExcluir = new JButton("Excluir Projeto");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelSuperior.add(painelBotoes, BorderLayout.NORTH);

        // --- Tabela de Projetos (precisa ser criada antes do painel de filtro) ---
        String[] colunas = {"ID", "Nome do Projeto", "Status", "Data de Início", "Data Prev. Fim", "Gerente"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProjetos = new JTable(tableModel);

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
        tabelaProjetos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        tabelaProjetos.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        // Adiciona os componentes à janela
        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões e Filtro ---
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getProjetoSelecionado()));
        btnExcluir.addActionListener(e -> excluirProjeto());

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
        carregarProjetos();
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

        btnNovo.setEnabled(podeGerenciar);
        btnEditar.setEnabled(podeGerenciar);
        btnExcluir.setEnabled(podeGerenciar);
    }

    private void carregarProjetos() {
        tableModel.setRowCount(0);
        this.projetosAtuais = projetoDAO.buscarTodosParaRelatorio();

        for (Projeto projeto : this.projetosAtuais) {
            Object[] rowData = {
                String.format("%03d", projeto.getId()), // Formata o ID
                projeto.getNomeProjeto(),
                projeto.getStatus(),
                projeto.getDataInicio(),
                projeto.getDataFimPrevista(),
                (projeto.getGerente() != null) ? projeto.getGerente().getNomeCompleto() : ""
            };
            tableModel.addRow(rowData);
        }
    }

    private void abrirFormulario(Projeto projeto) {
        if (projeto == null && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um projeto para editar.", "Nenhum Projeto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TelaFormularioProjeto formulario = new TelaFormularioProjeto(this, projeto);
        formulario.setVisible(true);

        if (formulario.isSalvo()) {
            carregarProjetos();
        }
    }

    private Projeto getProjetoSelecionado() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tabelaProjetos.convertRowIndexToModel(selectedRow);
            String idFormatado = (String) tableModel.getValueAt(modelRow, 0);
            int projetoId = Integer.parseInt(idFormatado); // Converte de volta para int
            return projetosAtuais.stream().filter(p -> p.getId() == projetoId).findFirst().orElse(null);
        }
        return null;
    }

    private void excluirProjeto() {
        Projeto projeto = getProjetoSelecionado();
        if (projeto != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o projeto '" + projeto.getNomeProjeto() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                projetoDAO.excluir(projeto.getId());
                carregarProjetos();
                aplicarFiltro();
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um projeto para excluir.", "Nenhum Projeto Selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }
}
