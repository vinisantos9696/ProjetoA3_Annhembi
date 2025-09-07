package br.com.projeto.view;

import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de formulário para criar ou editar um Usuário.
 */
public class TelaFormularioUsuario extends JDialog {

    private final JTextField txtNomeCompleto = new JTextField(30);
    private final JTextField txtUsername = new JTextField(30);
    private final JPasswordField txtSenha = new JPasswordField(30);
    private final JComboBox<String> cmbPerfil;

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final UsuarioDAO usuarioDAO;
    private Usuario usuario; // Nulo se for um novo usuário, preenchido se for edição
    private boolean salvo = false;

    public TelaFormularioUsuario(Frame owner, Usuario usuario) {
        super(owner, true); // Diálogo modal
        this.usuario = usuario;
        this.usuarioDAO = new UsuarioDAO();

        setTitle(usuario == null ? "Novo Usuário" : "Editar Usuário");
        setSize(450, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- Painel do Formulário ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels e Campos
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtNomeCompleto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Perfil:"), gbc);
        cmbPerfil = new JComboBox<>(new String[]{"administrador", "gerente", "colaborador"});
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbPerfil, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações e Preenchimento ---
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarUsuario());
    }

    private void preencherFormulario() {
        if (usuario != null) {
            txtNomeCompleto.setText(usuario.getNomeCompleto());
            txtUsername.setText(usuario.getUsername());
            cmbPerfil.setSelectedItem(usuario.getPerfil());
            // A senha não é preenchida por segurança
        }
    }

    private void salvarUsuario() {
        if (txtNomeCompleto.getText().trim().isEmpty() || txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome completo e username são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Valida a senha apenas para novos usuários
        if (usuario == null && new String(txtSenha.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "A senha é obrigatória para novos usuários.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.usuario == null) {
            this.usuario = new Usuario();
        }

        usuario.setNomeCompleto(txtNomeCompleto.getText().trim());
        usuario.setUsername(txtUsername.getText().trim());
        usuario.setPerfil((String) cmbPerfil.getSelectedItem());
        
        // Atualiza a senha apenas se uma nova for digitada
        String senha = new String(txtSenha.getPassword());
        if (!senha.trim().isEmpty()) {
            usuario.setSenha(senha);
        }

        usuarioDAO.salvar(usuario);

        this.salvo = true;
        dispose();
    }

    public boolean isSalvo() {
        return salvo;
    }
}
