package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaFormularioProjeto extends JDialog {

    private final JTextField txtNomeProjeto = new JTextField(40);
    private final JTextArea txtDescricao = new JTextArea(5, 40);
    private final JTextField txtDataInicio = new JTextField(10);
    private final JTextField txtDataFimPrevista = new JTextField(10);
    private final JComboBox<String> cmbStatus;
    private final JComboBox<Usuario> cmbGerente;

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final ProjetoDAO projetoDAO;
    private Projeto projeto;
    private boolean salvo = false;

    public TelaFormularioProjeto(Frame owner, Projeto projeto) {
        super(owner, true);
        this.projeto = projeto;
        this.projetoDAO = new ProjetoDAO();

        setTitle(projeto == null ? "Novo Projeto" : "Editar Projeto");
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome do Projeto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(txtNomeProjeto, gbc);
        gbc.weightx = 0.0;

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(txtDescricao), gbc);
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Data de Início (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtDataInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Data Prev. Fim (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtDataFimPrevista, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"planejado", "em andamento", "concluído", "cancelado"});
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Gerente:"), gbc);
        cmbGerente = new JComboBox<>();
        cmbGerente.setRenderer(new DefaultListCellRenderer() {
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
        gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbGerente, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        carregarGerentes();
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarProjeto());
    }

    private void carregarGerentes() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> gerentes = usuarioDAO.buscarGerentes();
        cmbGerente.addItem(null);
        for (Usuario gerente : gerentes) {
            cmbGerente.addItem(gerente);
        }
    }

    private void preencherFormulario() {
        if (projeto != null) {
            txtNomeProjeto.setText(projeto.getNomeProjeto());
            txtDescricao.setText(projeto.getDescricao());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (projeto.getDataInicio() != null) {
                txtDataInicio.setText(sdf.format(projeto.getDataInicio()));
            }
            if (projeto.getDataFimPrevista() != null) {
                txtDataFimPrevista.setText(sdf.format(projeto.getDataFimPrevista()));
            }

            cmbStatus.setSelectedItem(projeto.getStatus());

            if (projeto.getGerente() != null) {
                for (int i = 0; i < cmbGerente.getItemCount(); i++) {
                    Usuario item = cmbGerente.getItemAt(i);
                    if (item != null && item.getId() == projeto.getGerente().getId()) {
                        cmbGerente.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cmbGerente.setSelectedItem(null);
            }
        }
    }

    private void salvarProjeto() {
        if (txtNomeProjeto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do projeto é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.projeto == null) {
            this.projeto = new Projeto();
        }

        projeto.setNomeProjeto(txtNomeProjeto.getText().trim());
        projeto.setDescricao(txtDescricao.getText().trim());
        projeto.setStatus((String) cmbStatus.getSelectedItem());
        projeto.setGerente((Usuario) cmbGerente.getSelectedItem());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (!txtDataInicio.getText().trim().isEmpty()) {
                projeto.setDataInicio(sdf.parse(txtDataInicio.getText().trim()));
            } else {
                projeto.setDataInicio(null);
            }
            if (!txtDataFimPrevista.getText().trim().isEmpty()) {
                projeto.setDataFimPrevista(sdf.parse(txtDataFimPrevista.getText().trim()));
            } else {
                projeto.setDataFimPrevista(null);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/mm/aaaa.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        projetoDAO.salvar(projeto);

        this.salvo = true;
        dispose();
    }

    public boolean isSalvo() {
        return salvo;
    }
}
