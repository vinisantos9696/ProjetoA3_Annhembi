package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.TarefaDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Tarefa;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela de formulário para criar ou editar uma Tarefa.
 */
public class TelaFormularioTarefa extends JDialog {

    private final JTextArea txtDescricao = new JTextArea(5, 40);
    private final JComboBox<String> cmbStatus;
    private final JComboBox<Projeto> cmbProjeto;
    private final JComboBox<Usuario> cmbResponsavel;

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final TarefaDAO tarefaDAO;
    private Tarefa tarefa; // Nulo se for uma nova tarefa, preenchido se for edição
    private boolean salvo = false;

    public TelaFormularioTarefa(Frame owner, Tarefa tarefa) {
        super(owner, true); // Diálogo modal
        this.tarefa = tarefa;
        this.tarefaDAO = new TarefaDAO();

        setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- Painel do Formulário ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels e Campos
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH; formPanel.add(new JScrollPane(txtDescricao), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Planejado", "Em Andamento", "Concluído"});
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Projeto:"), gbc);
        cmbProjeto = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbProjeto, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Responsável:"), gbc);
        cmbResponsavel = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbResponsavel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações e Preenchimento ---
        carregarProjetos();
        carregarResponsaveis();
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarTarefa());
    }

    private void carregarProjetos() {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        List<Projeto> projetos = projetoDAO.buscarTodosParaRelatorio(); // Reutiliza o método existente
        for (Projeto projeto : projetos) {
            cmbProjeto.addItem(projeto);
        }
    }

    private void carregarResponsaveis() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.buscarTodos(); // Reutiliza o método existente
        for (Usuario usuario : usuarios) {
            cmbResponsavel.addItem(usuario);
        }
    }

    private void preencherFormulario() {
        if (tarefa != null) {
            txtDescricao.setText(tarefa.getDescricao());
            cmbStatus.setSelectedItem(tarefa.getStatus());

            if (tarefa.getProjeto() != null) {
                for (int i = 0; i < cmbProjeto.getItemCount(); i++) {
                    if (cmbProjeto.getItemAt(i).getId() == tarefa.getProjeto().getId()) {
                        cmbProjeto.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (tarefa.getResponsavel() != null) {
                for (int i = 0; i < cmbResponsavel.getItemCount(); i++) {
                    if (cmbResponsavel.getItemAt(i).getId() == tarefa.getResponsavel().getId()) {
                        cmbResponsavel.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void salvarTarefa() {
        if (txtDescricao.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "A descrição da tarefa é obrigatória.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.tarefa == null) {
            this.tarefa = new Tarefa();
        }

        tarefa.setDescricao(txtDescricao.getText().trim());
        tarefa.setStatus((String) cmbStatus.getSelectedItem());
        tarefa.setProjeto((Projeto) cmbProjeto.getSelectedItem());
        tarefa.setResponsavel((Usuario) cmbResponsavel.getSelectedItem());

        tarefaDAO.salvar(tarefa);

        this.salvo = true;
        dispose();
    }

    public boolean isSalvo() {
        return salvo;
    }
}
