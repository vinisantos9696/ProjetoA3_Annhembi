package br.com.projeto.controller;

import br.com.projeto.database.DatabaseConnection;
import br.com.projeto.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO (Data Access Object) para a entidade User.
 * Contém a lógica para interagir com a tabela 'usuarios' no banco de dados.
 */
public class UserDAO {

    /**
     * Valida as credenciais de um usuário no banco de dados.
     *
     * @param login O login do usuário.
     * @param password A senha do usuário.
     * @return Um objeto User se as credenciais forem válidas, caso contrário, null.
     */
    public User validateUser(String login, String password) {
        // SQL para selecionar o usuário com base no login e senha
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
        User user = null;

        // O try-with-resources garante que a conexão e o PreparedStatement sejam fechados
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define os parâmetros da consulta
            pstmt.setString(1, login);
            pstmt.setString(2, password); // Em um sistema real, a senha seria comparada com um hash

            // Executa a consulta
            try (ResultSet rs = pstmt.executeQuery()) {
                // Se um registro for encontrado, cria o objeto User
                if (rs.next()) {
                    user = new User(
                        rs.getInt("id"),
                        rs.getString("nome_completo"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("perfil")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Em uma aplicação real, use um logger
        }
        return user;
    }
}