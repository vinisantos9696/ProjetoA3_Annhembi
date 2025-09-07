package br.com.projeto.view;

import br.com.projeto.controller.UserDAO;
import br.com.projeto.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela de Login da aplicação.
 */
public class LoginScreen extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;

    public LoginScreen() {
        userDAO = new UserDAO();

        // Configurações da janela
        setTitle("Login - Sistema de Gestão de Projetos");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela
        setLayout(new BorderLayout(10, 10));

        // Painel do formulário com GridBagLayout para mais controle
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes do formulário
        JLabel titleLabel = new JLabel("Acessar Sistema", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        loginField = new JTextField(15);
        formPanel.add(loginField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Entrar");
        buttonPanel.add(loginButton);

        // Adiciona os painéis à janela
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ação do botão de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    /**
     * Lida com a tentativa de login.
     */
    private void handleLogin() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.validateUser(login, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido! Bem-vindo, " + user.getFullName() + ".", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            // Aqui você abriria a tela principal (Dashboard)
            // new MainDashboard(user).setVisible(true);
            dispose(); // Fecha a tela de login
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha inválidos.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }
}