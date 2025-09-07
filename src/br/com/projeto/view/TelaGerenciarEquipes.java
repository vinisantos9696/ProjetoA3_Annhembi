package br.com.projeto.view;

import br.com.projeto.dao.EquipeDAO;
import br.com.projeto.model.Equipe;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    private final JButton btnNova;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    public TelaGerenciarEquipes(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.equipeDAO = new EquipeDAO();

        setTitle("Gerenciamento de Equipes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNova = new JButton("Nova Equipe");
        btnEditar = new JButton("Editar Equipe");
        btnExcluir = new JButton("Excluir Equipe");

        painelBotoes.add(btnNova);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // --- Tabela de Equipes ---
        String[] colunas = {"ID", "Nome da Equipe"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEquipes = new JTable(tableModel);
        tabelaEquipes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões ---
        btnNova.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getEquipeSelecionada()));
        btnExcluir.addActionListener(e -> excluirEquipe());

        // Carrega os dados iniciais e aplica as permissões
        carregarEquipes();
        configurarPermissoes();
    }

    /**
     * Habilita ou desabilita os botões de gerenciamento com base no perfil do usuário.
     */
    private void configurarPermissoes() {
        String perfil = usuarioLogado.getPerfil();
        boolean podeGerenciar = "administrador".equalsIgnoreCase(perfil) || "gerente".equalsIgnoreCase(perfil);

        btnNova.setEnabled(podeGerenciar);
        btnEditar.setEnabled(podeGerenciar);
        btnExcluir.setEnabled(podeGerenciar);
    }

    /**
     * Carrega a lista de equipes do banco de dados e atualiza a tabela.
     */
    private void carregarEquipes() {
        tableModel.setRowCount(0);
        this.equipesAtuais = equipeDAO.buscarTodas();
        
        for (Equipe equipe : this.equipesAtuais) {
            Object[] rowData = { equipe.getId(), equipe.getNome() };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Abre o formulário para criar ou editar uma equipe.
     * @param equipe A equipe a ser editada, ou null para uma nova.
     */
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

    /**
     * Pega o objeto Equipe correspondente à linha selecionada na tabela.
     * @return A equipe selecionada, ou null se nenhuma linha for selecionada.
     */
    private Equipe getEquipeSelecionada() {
        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow >= 0) {
            int equipeId = (int) tableModel.getValueAt(selectedRow, 0);
            // Encontra a equipe na lista local para garantir que o objeto esteja completo
            return equipesAtuais.stream().filter(e -> e.getId() == equipeId).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Exclui a equipe selecionada na tabela após confirmação.
     */
    private void excluirEquipe() {
        Equipe equipe = getEquipeSelecionada();
        if (equipe != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a equipe '" + equipe.getNome() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                equipeDAO.excluir(equipe.getId());
                carregarEquipes();
                JOptionPane.showMessageDialog(this, "Equipe excluída com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma equipe para excluir.", "Nenhuma Equipe Selecionada", JOptionPane.WARNING_MESSAGE);
        }
    }
}
