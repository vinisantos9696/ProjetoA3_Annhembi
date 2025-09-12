package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.TarefaDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Tarefa;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaFormularioTarefa extends JDialog {

    private final JTextField txtTitulo = new JTextField(40);
    private final JTextArea txtDescricao = new JTextArea(5, 40);
    private final JComboBox<String> cmbStatus;
    private final JComboBox<Projeto> cmbProjeto;
    private final JComboBox<Usuario> cmbResponsavel;
    private final JTextField txtDataInicioPrevista = new JTextField(10);
    private final JTextField txtDataFimPrevista = new JTextField(10);
    private final JTextField txtDataInicioReal = new JTextField(10);
    private final JTextField txtDataFimReal = new JTextField(10);

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final TarefaDAO tarefaDAO;
    private Tarefa tarefa;
    private boolean salvo = false;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TelaFormularioTarefa(Frame owner, Tarefa tarefa) {
        super(owner, true);
        this.tarefa = tarefa;
        this.tarefaDAO = new TarefaDAO();

        setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");
        setSize(500, 650); // Janela mais alta e um pouco mais estreita
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Layout de 2 colunas (Rótulo e Campo)

        // Linha 0: Título
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(txtTitulo, gbc);

        // Linha 1: Descrição
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; formPanel.add(new JScrollPane(txtDescricao), gbc);
        gbc.weighty = 0.0;

        // Linha 2: Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"pendente", "em execução", "concluída"});
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbStatus, gbc);

        // Linha 3: Projeto
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Projeto:"), gbc);
        cmbProjeto = createProjetoComboBox();
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(cmbProjeto, gbc);

        // Linha 4: Responsável
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Responsável:"), gbc);
        cmbResponsavel = createUsuarioComboBox();
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(cmbResponsavel, gbc);

        // --- Linhas de Datas (Reorganizado) ---
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Início Previsto (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(txtDataInicioPrevista, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Fim Previsto (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(txtDataFimPrevista, gbc);

        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Início Real (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(txtDataInicioReal, gbc);

        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(new JLabel("Fim Real (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 8; formPanel.add(txtDataFimReal, gbc);

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

            // Preenche datas
            txtDataInicioPrevista.setText(formatDate(tarefa.getDataInicioPrevista()));
            txtDataFimPrevista.setText(formatDate(tarefa.getDataFimPrevista()));
            txtDataInicioReal.setText(formatDate(tarefa.getDataInicioReal()));
            txtDataFimReal.setText(formatDate(tarefa.getDataFimReal()));

            selecionarItemNoComboBox(cmbProjeto, tarefa.getProjeto());
            selecionarItemNoComboBox(cmbResponsavel, tarefa.getResponsavel());
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

        try {
            tarefa.setTitulo(txtTitulo.getText().trim());
            tarefa.setDescricao(txtDescricao.getText().trim());
            tarefa.setStatus((String) cmbStatus.getSelectedItem());
            tarefa.setProjeto((Projeto) cmbProjeto.getSelectedItem());
            tarefa.setResponsavel((Usuario) cmbResponsavel.getSelectedItem());

            // Parse e define as datas
            tarefa.setDataInicioPrevista(parseDate(txtDataInicioPrevista.getText()));
            tarefa.setDataFimPrevista(parseDate(txtDataFimPrevista.getText()));
            tarefa.setDataInicioReal(parseDate(txtDataInicioReal.getText()));
            tarefa.setDataFimReal(parseDate(txtDataFimReal.getText()));

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-MM-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return; // Impede o salvamento
        }

        tarefaDAO.salvar(tarefa);

        this.salvo = true;
        dispose();
    }

    // --- Métodos Utilitários ---

    private String formatDate(Date date) {
        if (date == null) return "";
        return dateFormat.format(date);
    }

    private Date parseDate(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return dateFormat.parse(dateString.trim());
    }

    private <T> void selecionarItemNoComboBox(JComboBox<T> comboBox, T itemParaSelecionar) {
        if (itemParaSelecionar == null) {
            comboBox.setSelectedItem(null);
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item != null && item.equals(itemParaSelecionar)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private JComboBox<Projeto> createProjetoComboBox() {
        JComboBox<Projeto> comboBox = new JComboBox<>();
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Projeto ? ((Projeto) value).getNomeProjeto() : "Nenhum");
                return this;
            }
        });
        return comboBox;
    }

    private JComboBox<Usuario> createUsuarioComboBox() {
        JComboBox<Usuario> comboBox = new JComboBox<>();
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Usuario ? ((Usuario) value).getNomeCompleto() : "Nenhum");
                return this;
            }
        });
        return comboBox;
    }

    public boolean isSalvo() {
        return salvo;
    }
}
