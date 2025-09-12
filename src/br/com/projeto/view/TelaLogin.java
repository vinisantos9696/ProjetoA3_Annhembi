package br.com.projeto.view;

import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaLogin extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public TelaLogin() {
        setTitle("Login - Sistema de Gestão de Projetos");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Painel principal com um layout mais organizado
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label e campo de texto para o usuário
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa duas colunas
        txtLogin = new JTextField(20);
        panel.add(txtLogin, gbc);

        // Label e campo de texto para a senha
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reseta para uma coluna
        panel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        txtPassword = new JPasswordField(20);
        panel.add(txtPassword, gbc);

        // Botão de login
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza o botão
        btnLogin = new JButton("Entrar");
        panel.add(btnLogin, gbc);

        // Adiciona o painel à janela
        add(panel);

        // Ação do botão de login
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        // Define o botão de login como o padrão da janela (acionado com Enter)
        this.getRootPane().setDefaultButton(btnLogin);
    }

    private void realizarLogin() {
        String login = txtLogin.getText();
        String password = new String(txtPassword.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login e senha devem ser preenchidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioLogado = usuarioDAO.fazerLogin(login, password);

        if (usuarioLogado != null) {
            // Abre a tela principal, passando o usuário que fez o login
            TelaPrincipal telaPrincipal = new TelaPrincipal(usuarioLogado);
            telaPrincipal.setVisible(true);

            // Fecha a tela de login
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}
