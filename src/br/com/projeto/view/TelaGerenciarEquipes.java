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
        setSize(800, 400);
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
        String[] colunas = {"ID", "Nome da Equipe", "Descrição"};
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
        btnEditar.addActionListener(e -> {
            Equipe equipeSelecionada = getEquipeSelecionada();
            if (equipeSelecionada != null) {
                abrirFormulario(equipeSelecionada);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma equipe para editar.", "Nenhuma Equipe Selecionada", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnExcluir.addActionListener(e -> excluirEquipe());

        // Carrega os dados iniciais e aplica as permissões
        carregarEquipes();
        configurarPermissoes();
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
            Object[] rowData = { equipe.getIdEquipe(), equipe.getNomeEquipe(), equipe.getDescricao() };
            tableModel.addRow(rowData);
        }
    }

    private void abrirFormulario(Equipe equipe) {
        TelaFormularioEquipe formulario = new TelaFormularioEquipe(this, equipe);
        formulario.setVisible(true);

        if (formulario.isSalvo()) {
            carregarEquipes();
        }
    }

    private Equipe getEquipeSelecionada() {
        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow >= 0) {
            int equipeId = (int) tableModel.getValueAt(selectedRow, 0);
            // Usa o método buscarPorId para carregar o objeto completo com membros e projetos
            return equipeDAO.buscarPorId(equipeId);
        }
        return null;
    }

    private void excluirEquipe() {
        int selectedRow = tabelaEquipes.getSelectedRow();
        if (selectedRow >= 0) {
            int equipeId = (int) tableModel.getValueAt(selectedRow, 0);
            String nomeEquipe = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a equipe '" + nomeEquipe + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                equipeDAO.excluir(equipeId);
                carregarEquipes();
                JOptionPane.showMessageDialog(this, "Equipe excluída com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma equipe para excluir.", "Nenhuma Equipe Selecionada", JOptionPane.WARNING_MESSAGE);
        }
    }
}
