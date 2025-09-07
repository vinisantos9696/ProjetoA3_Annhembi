package br.com.projeto.main;

import br.com.projeto.database.DatabaseSetup;
import br.com.projeto.view.TelaLogin;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // 1. Garante que o banco de dados e as tabelas estejam prontos.
        try {
            DatabaseSetup.setupDatabase();
        } catch (Exception e) {
            System.err.println("Falha crítica na inicialização do banco de dados. A aplicação será encerrada.");
            e.printStackTrace();
            return; // Encerra a aplicação se o BD falhar
        }

        // 2. Inicia a interface gráfica na thread de eventos do Swing.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaLogin telaLogin = new TelaLogin();
                telaLogin.setVisible(true);
            }
        });
    }
}
