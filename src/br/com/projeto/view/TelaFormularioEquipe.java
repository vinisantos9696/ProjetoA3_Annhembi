package br.com.projeto.view;

import br.com.projeto.dao.EquipeDAO;
import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Equipe;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaFormularioEquipe extends JDialog {

    // --- DAOs ---
    private final EquipeDAO equipeDAO;
    private final UsuarioDAO usuarioDAO;
    private final ProjetoDAO projetoDAO;

    // --- Model ---
    private Equipe equipe;
    private boolean salvo = false;

    // --- Componentes de Detalhes ---
    private final JTextField txtNomeEquipe = new JTextField(30);
    private final JTextArea txtDescricao = new JTextArea(4, 30);

    // --- Componentes de Membros ---
    private DefaultListModel<Usuario> membrosDisponiveisModel;
    private DefaultListModel<Usuario> membrosDaEquipeModel;
    private JList<Usuario> listMembrosDisponiveis;
    private JList<Usuario> listMembrosDaEquipe;

    // --- Componentes de Projetos ---
    private DefaultListModel<Projeto> projetosDisponiveisModel;
    private DefaultListModel<Projeto> projetosAlocadosModel;
    private JList<Projeto> listProjetosDisponiveis;
    private JList<Projeto> listProjetosAlocados;

    public TelaFormularioEquipe(Frame owner, Equipe equipe) {
        super(owner, true);
        this.equipe = (equipe != null) ? equipe : new Equipe();
        this.equipeDAO = new EquipeDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.projetoDAO = new ProjetoDAO();

        setTitle(this.equipe.getIdEquipe() == 0 ? "Nova Equipe" : "Editar Equipe");
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- Abas ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Detalhes da Equipe", createDetalhesPanel());
        tabbedPane.addTab("Gerenciar Membros", createMembrosPanel());
        tabbedPane.addTab("Alocar Projetos", createProjetosPanel());

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttonPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        // Adiciona os componentes ao diálogo
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações e Carregamento ---
        loadInitialData();
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarEquipe());
    }

    private JPanel createDetalhesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome da Equipe:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(txtNomeEquipe, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        panel.add(new JScrollPane(txtDescricao), gbc);

        return panel;
    }

    private JPanel createMembrosPanel() {
        membrosDisponiveisModel = new DefaultListModel<>();
        membrosDaEquipeModel = new DefaultListModel<>();

        listMembrosDisponiveis = new JList<>(membrosDisponiveisModel);
        listMembrosDaEquipe = new JList<>(membrosDaEquipeModel);

        setUsuarioListRenderer(listMembrosDisponiveis);
        setUsuarioListRenderer(listMembrosDaEquipe);

        JButton btnAdicionar = new JButton(">>");
        JButton btnRemover = new JButton("<<");

        btnAdicionar.addActionListener(e -> moverItens(listMembrosDisponiveis, membrosDisponiveisModel, membrosDaEquipeModel));
        btnRemover.addActionListener(e -> moverItens(listMembrosDaEquipe, membrosDaEquipeModel, membrosDisponiveisModel));

        return createSelectionPanel(listMembrosDisponiveis, listMembrosDaEquipe, btnAdicionar, btnRemover, "Usuários Disponíveis", "Membros da Equipe");
    }

    private JPanel createProjetosPanel() {
        projetosDisponiveisModel = new DefaultListModel<>();
        projetosAlocadosModel = new DefaultListModel<>();

        listProjetosDisponiveis = new JList<>(projetosDisponiveisModel);
        listProjetosAlocados = new JList<>(projetosAlocadosModel);

        setProjetoListRenderer(listProjetosDisponiveis);
        setProjetoListRenderer(listProjetosAlocados);

        JButton btnAdicionar = new JButton(">>");
        JButton btnRemover = new JButton("<<");

        btnAdicionar.addActionListener(e -> moverItens(listProjetosDisponiveis, projetosDisponiveisModel, projetosAlocadosModel));
        btnRemover.addActionListener(e -> moverItens(listProjetosAlocados, projetosAlocadosModel, projetosDisponiveisModel));

        return createSelectionPanel(listProjetosDisponiveis, listProjetosAlocados, btnAdicionar, btnRemover, "Projetos Disponíveis", "Projetos Alocados");
    }

    private <T> JPanel createSelectionPanel(JList<T> listDisponiveis, JList<T> listSelecionados, JButton btnAdicionar, JButton btnRemover, String tituloDisponiveis, String tituloSelecionados) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(createTitledScrollPane(listDisponiveis, tituloDisponiveis), gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 0, 5, 0);
        btnGbc.gridx = 0; btnGbc.gridy = 0; buttonPanel.add(btnAdicionar, btnGbc);
        btnGbc.gridy = 1; buttonPanel.add(btnRemover, btnGbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        panel.add(buttonPanel, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(createTitledScrollPane(listSelecionados, tituloSelecionados), gbc);

        return panel;
    }

    private void loadInitialData() {
        // Carrega todos os usuários e projetos
        List<Usuario> todosUsuarios = usuarioDAO.buscarTodos();
        List<Projeto> todosProjetos = projetoDAO.buscarTodosParaRelatorio();

        // Popula as listas de disponíveis
        todosUsuarios.forEach(membrosDisponiveisModel::addElement);
        todosProjetos.forEach(projetosDisponiveisModel::addElement);
    }

    private void preencherFormulario() {
        txtNomeEquipe.setText(equipe.getNomeEquipe());
        txtDescricao.setText(equipe.getDescricao());

        // Move os membros e projetos já associados para as listas da direita
        if (equipe.getMembros() != null) {
            for (Usuario membro : equipe.getMembros()) {
                if (membrosDisponiveisModel.contains(membro)) {
                    membrosDisponiveisModel.removeElement(membro);
                    membrosDaEquipeModel.addElement(membro);
                }
            }
        }
        if (equipe.getProjetosAlocados() != null) {
            for (Projeto projeto : equipe.getProjetosAlocados()) {
                if (projetosDisponiveisModel.contains(projeto)) {
                    projetosDisponiveisModel.removeElement(projeto);
                    projetosAlocadosModel.addElement(projeto);
                }
            }
        }
    }

    private void salvarEquipe() {
        if (txtNomeEquipe.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da equipe é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        equipe.setNomeEquipe(txtNomeEquipe.getText().trim());
        equipe.setDescricao(txtDescricao.getText().trim());

        List<Usuario> membrosFinais = new ArrayList<>();
        for (int i = 0; i < membrosDaEquipeModel.size(); i++) {
            membrosFinais.add(membrosDaEquipeModel.getElementAt(i));
        }
        equipe.setMembros(membrosFinais);

        List<Projeto> projetosFinais = new ArrayList<>();
        for (int i = 0; i < projetosAlocadosModel.size(); i++) {
            projetosFinais.add(projetosAlocadosModel.getElementAt(i));
        }
        equipe.setProjetosAlocados(projetosFinais);

        equipeDAO.salvar(equipe);

        this.salvo = true;
        dispose();
    }

    private <T> void moverItens(JList<T> sourceList, DefaultListModel<T> sourceModel, DefaultListModel<T> destModel) {
        List<T> itemsToMove = sourceList.getSelectedValuesList();
        for (T item : itemsToMove) {
            sourceModel.removeElement(item);
            destModel.addElement(item);
        }
    }

    private JScrollPane createTitledScrollPane(Component view, String title) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(new TitledBorder(title));
        return scrollPane;
    }

    private void setUsuarioListRenderer(JList<Usuario> list) {
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    setText(((Usuario) value).getNomeCompleto());
                }
                return this;
            }
        });
    }

    private void setProjetoListRenderer(JList<Projeto> list) {
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                if (value instanceof Projeto) {
                    setText(((Projeto) value).getNomeProjeto());
                }
                return this;
            }
        });
    }

    public boolean isSalvo() {
        return salvo;
    }
}