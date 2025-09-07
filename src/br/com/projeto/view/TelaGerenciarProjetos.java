package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    public TelaGerenciarProjetos(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.projetoDAO = new ProjetoDAO();

        setTitle("Gerenciamento de Projetos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo Projeto");
        btnEditar = new JButton("Editar Projeto");
        btnExcluir = new JButton("Excluir Projeto");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // --- Tabela de Projetos ---
        String[] colunas = {"ID", "Nome", "Status", "Data de Início", "Data de Fim", "Gerente"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaProjetos = new JTable(tableModel);
        tabelaProjetos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Apenas uma linha pode ser selecionada

        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // TODO: Adicionar ações para os botões de novo, editar e excluir.

        // Carrega os dados iniciais e aplica as permissões
        carregarProjetos();
        configurarPermissoes();
    }

    /**
     * Habilita ou desabilita os botões de gerenciamento com base no perfil do usuário.
     */
    private void configurarPermissoes() {
        String perfil = usuarioLogado.getPerfil();
        boolean podeGerenciar = "administrador".equalsIgnoreCase(perfil) || "gerente".equalsIgnoreCase(perfil);

        btnNovo.setEnabled(podeGerenciar);
        btnEditar.setEnabled(podeGerenciar);
        btnExcluir.setEnabled(podeGerenciar);
    }

    /**
     * Carrega a lista de projetos do banco de dados e atualiza a tabela.
     */
    private void carregarProjetos() {
        // Limpa a tabela atual
        tableModel.setRowCount(0);

        // Reutiliza o método do DAO que busca todos os projetos para o relatório
        List<Projeto> projetos = projetoDAO.buscarTodosParaRelatorio();

        for (Projeto projeto : projetos) {
            Object[] rowData = {
                projeto.getId(),
                projeto.getNome(),
                projeto.getStatus(),
                projeto.getDataInicio(),
                projeto.getDataFim(),
                (projeto.getGerente() != null) ? projeto.getGerente().getNomeCompleto() : ""
            };
            tableModel.addRow(rowData);
        }
    }
}
