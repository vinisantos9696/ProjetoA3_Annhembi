package br.com.projeto.view;

import br.com.projeto.dao.EquipeDAO;
import br.com.projeto.model.Equipe;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de formulário para criar ou editar uma Equipe.
 */
public class TelaFormularioEquipe extends JDialog {

    private final JTextField txtNome = new JTextField(30);
    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final EquipeDAO equipeDAO;
    private Equipe equipe; // Nulo se for uma nova equipe, preenchido se for edição
    private boolean salvo = false;

    public TelaFormularioEquipe(Frame owner, Equipe equipe) {
        super(owner, true); // Diálogo modal
        this.equipe = equipe;
        this.equipeDAO = new EquipeDAO();

        setTitle(equipe == null ? "Nova Equipe" : "Editar Equipe");
        setSize(400, 150);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- Painel do Formulário ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome da Equipe:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtNome, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações e Preenchimento ---
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarEquipe());
    }

    /**
     * Se for uma edição, preenche o campo com o nome da equipe existente.
     */
    private void preencherFormulario() {
        if (equipe != null) {
            txtNome.setText(equipe.getNome());
        }
    }

    /**
     * Valida os dados, cria ou atualiza o objeto Equipe e o salva no banco.
     */
    private void salvarEquipe() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da equipe é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.equipe == null) {
            this.equipe = new Equipe();
        }

        equipe.setNome(txtNome.getText().trim());

        // O método salvar do DAO já trata se é um INSERT ou UPDATE
        equipeDAO.salvar(equipe);

        this.salvo = true;
        dispose();
    }

    public boolean isSalvo() {
        return salvo;
    }
}
