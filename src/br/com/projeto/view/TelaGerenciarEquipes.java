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
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaEquipes = new JTable(tableModel);
        tabelaEquipes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // TODO: Adicionar ações para os botões de nova, editar e excluir.

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
        // Limpa a tabela atual
        tableModel.setRowCount(0);

        List<Equipe> equipes = equipeDAO.buscarTodas();
        
        for (Equipe equipe : equipes) {
            Object[] rowData = { equipe.getId(), equipe.getNome() };
            tableModel.addRow(rowData);
        }
    }
}
