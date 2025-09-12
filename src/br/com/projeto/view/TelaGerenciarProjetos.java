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
    private List<Projeto> projetosAtuais; // Lista para manter os projetos carregados

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
        String[] colunas = {"ID", "Nome do Projeto", "Status", "Data de Início", "Data Prev. Fim", "Gerente"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna todas as células não editáveis
            }
        };
        tabelaProjetos = new JTable(tableModel);
        tabelaProjetos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões ---
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getProjetoSelecionado()));
        btnExcluir.addActionListener(e -> excluirProjeto());

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
        tableModel.setRowCount(0);
        this.projetosAtuais = projetoDAO.buscarTodosParaRelatorio();

        for (Projeto projeto : this.projetosAtuais) {
            Object[] rowData = {
                projeto.getId(),
                projeto.getNomeProjeto(),
                projeto.getStatus(),
                projeto.getDataInicio(),
                projeto.getDataFimPrevista(),
                (projeto.getGerente() != null) ? projeto.getGerente().getNomeCompleto() : ""
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Abre o formulário para criar um novo projeto ou editar um existente.
     * @param projeto O projeto a ser editado, ou null para um novo projeto.
     */
    private void abrirFormulario(Projeto projeto) {
        // Para o botão "Editar", verifica se um projeto foi selecionado
        if (projeto == null && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um projeto para editar.", "Nenhum Projeto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TelaFormularioProjeto formulario = new TelaFormularioProjeto(this, projeto);
        formulario.setVisible(true);

        // Se o formulário foi salvo, atualiza a tabela
        if (formulario.isSalvo()) {
            carregarProjetos();
        }
    }

    /**
     * Pega o objeto Projeto correspondente à linha selecionada na tabela.
     * @return O projeto selecionado, ou null se nenhuma linha for selecionada.
     */
    private Projeto getProjetoSelecionado() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow >= 0) {
            int projetoId = (int) tableModel.getValueAt(selectedRow, 0);
            return projetosAtuais.stream().filter(p -> p.getId() == projetoId).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Exclui o projeto selecionado na tabela após confirmação.
     */
    private void excluirProjeto() {
        Projeto projeto = getProjetoSelecionado();
        if (projeto != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o projeto '" + projeto.getNomeProjeto() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                projetoDAO.excluir(projeto.getId());
                carregarProjetos(); // Atualiza a tabela
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um projeto para excluir.", "Nenhum Projeto Selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }
}
