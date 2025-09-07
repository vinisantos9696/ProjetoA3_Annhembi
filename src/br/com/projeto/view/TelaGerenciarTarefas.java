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
    private List<Tarefa> tarefasAtuais;

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
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaTarefas = new JTable(tableModel);
        tabelaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões ---
        btnNova.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getTarefaSelecionada()));
        btnExcluir.addActionListener(e -> excluirTarefa());

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
        tableModel.setRowCount(0);
        this.tarefasAtuais = tarefaDAO.buscarTodas();
        
        for (Tarefa tarefa : this.tarefasAtuais) {
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

    /**
     * Abre o formulário para criar ou editar uma tarefa.
     * @param tarefa A tarefa a ser editada, ou null para uma nova.
     */
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

    /**
     * Pega o objeto Tarefa correspondente à linha selecionada na tabela.
     * @return A tarefa selecionada, ou null se nenhuma linha for selecionada.
     */
    private Tarefa getTarefaSelecionada() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow >= 0) {
            int tarefaId = (int) tableModel.getValueAt(selectedRow, 0);
            return tarefasAtuais.stream().filter(t -> t.getId() == tarefaId).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Exclui a tarefa selecionada na tabela após confirmação.
     */
    private void excluirTarefa() {
        Tarefa tarefa = getTarefaSelecionada();
        if (tarefa != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a tarefa: '" + tarefa.getDescricao() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                tarefaDAO.excluir(tarefa.getId());
                carregarTarefas();
                JOptionPane.showMessageDialog(this, "Tarefa excluída com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para excluir.", "Nenhuma Tarefa Selecionada", JOptionPane.WARNING_MESSAGE);
        }
    }
}
