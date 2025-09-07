package br.com.projeto.view;

import br.com.projeto.dao.TarefaDAO;
import br.com.projeto.model.Tarefa;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    private final JButton btnNova;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    public TelaGerenciarTarefas(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.tarefaDAO = new TarefaDAO();

        setTitle("Gerenciamento de Tarefas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNova = new JButton("Nova Tarefa");
        btnEditar = new JButton("Editar Tarefa");
        btnExcluir = new JButton("Excluir Tarefa");

        painelBotoes.add(btnNova);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // --- Tabela de Tarefas ---
        String[] colunas = {"ID", "Descrição", "Status", "Projeto", "Responsável"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaTarefas = new JTable(tableModel);
        tabelaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // TODO: Adicionar ações para os botões de nova, editar e excluir.

        // Carrega os dados iniciais e aplica as permissões
        carregarTarefas();
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
     * Carrega a lista de tarefas do banco de dados e atualiza a tabela.
     */
    private void carregarTarefas() {
        // Limpa a tabela atual
        tableModel.setRowCount(0);

        List<Tarefa> tarefas = tarefaDAO.buscarTodas();
        
        for (Tarefa tarefa : tarefas) {
            Object[] rowData = { 
                tarefa.getId(), 
                tarefa.getDescricao(), 
                tarefa.getStatus(),
                (tarefa.getProjeto() != null) ? tarefa.getProjeto().getNome() : "N/A",
                (tarefa.getResponsavel() != null) ? tarefa.getResponsavel().getNomeCompleto() : "N/A"
            };
            tableModel.addRow(rowData);
        }
    }
}
