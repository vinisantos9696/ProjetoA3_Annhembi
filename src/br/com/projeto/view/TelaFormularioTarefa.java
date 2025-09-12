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

public class TelaFormularioTarefa extends JDialog {

    private final JTextField txtTitulo = new JTextField(40);
    private final JTextArea txtDescricao = new JTextArea(5, 40);
    private final JComboBox<String> cmbStatus;
    private final JComboBox<Projeto> cmbProjeto;
    private final JComboBox<Usuario> cmbResponsavel;

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final TarefaDAO tarefaDAO;
    private Tarefa tarefa;
    private boolean salvo = false;

    public TelaFormularioTarefa(Frame owner, Tarefa tarefa) {
        super(owner, true);
        this.tarefa = tarefa;
        this.tarefaDAO = new TarefaDAO();

        setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");
        setSize(500, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(txtTitulo, gbc);
        gbc.weightx = 0.0;

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0; formPanel.add(new JScrollPane(txtDescricao), gbc);
        gbc.weightx = 0.0; gbc.weighty = 0.0;

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"pendente", "em execução", "concluída"});
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Projeto:"), gbc);
        cmbProjeto = new JComboBox<>();
        cmbProjeto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Projeto) {
                    setText(((Projeto) value).getNomeProjeto());
                } else {
                    setText("Nenhum");
                }
                return this;
            }
        });
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbProjeto, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Responsável:"), gbc);
        cmbResponsavel = new JComboBox<>();
        cmbResponsavel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    setText(((Usuario) value).getNomeCompleto());
                } else {
                    setText("Nenhum");
                }
                return this;
            }
        });
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbResponsavel, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        carregarProjetos();
        carregarResponsaveis();
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarTarefa());
    }

    private void carregarProjetos() {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        List<Projeto> projetos = projetoDAO.buscarTodosParaRelatorio();
        cmbProjeto.addItem(null);
        for (Projeto projeto : projetos) {
            cmbProjeto.addItem(projeto);
        }
    }

    private void carregarResponsaveis() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.buscarTodos();
        cmbResponsavel.addItem(null);
        for (Usuario usuario : usuarios) {
            cmbResponsavel.addItem(usuario);
        }
    }

    private void preencherFormulario() {
        if (tarefa != null) {
            txtTitulo.setText(tarefa.getTitulo());
            txtDescricao.setText(tarefa.getDescricao());
            cmbStatus.setSelectedItem(tarefa.getStatus());

            if (tarefa.getProjeto() != null) {
                for (int i = 0; i < cmbProjeto.getItemCount(); i++) {
                    Projeto item = cmbProjeto.getItemAt(i);
                    if (item != null && item.getId() == tarefa.getProjeto().getId()) {
                        cmbProjeto.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cmbProjeto.setSelectedItem(null);
            }

            if (tarefa.getResponsavel() != null) {
                for (int i = 0; i < cmbResponsavel.getItemCount(); i++) {
                    Usuario item = cmbResponsavel.getItemAt(i);
                    if (item != null && item.getId() == tarefa.getResponsavel().getId()) {
                        cmbResponsavel.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cmbResponsavel.setSelectedItem(null);
            }
        }
    }

    private void salvarTarefa() {
        if (txtTitulo.getText().trim().isEmpty() || cmbProjeto.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "O título e o projeto são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.tarefa == null) {
            this.tarefa = new Tarefa();
        }

        tarefa.setTitulo(txtTitulo.getText().trim());
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
