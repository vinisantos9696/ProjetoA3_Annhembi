package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Usuario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /**
     * Valida as credenciais do usuário no banco de dados.
     * @param username O nome de usuário.
     * @param senha A senha.
     * @return Um objeto Usuario se as credenciais forem válidas, caso contrário, null.
     */
    public Usuario fazerLogin(String username, String senha) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND senha = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome_completo"),
                        rs.getString("username"),
                        rs.getString("senha"),
                        rs.getString("perfil")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao tentar fazer login: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Retorna null se o login falhar
    }

    /**
     * Salva um novo usuário no banco de dados ou atualiza um existente.
     * @param usuario O objeto Usuario a ser salvo.
     */
    public void salvar(Usuario usuario) {
        // Se o ID for 0, é um novo usuário (INSERT), senão, é uma atualização (UPDATE)
        String sql = (usuario.getId() == 0)
            ? "INSERT INTO usuarios (nome_completo, username, senha, perfil) VALUES (?, ?, ?, ?)"
            : "UPDATE usuarios SET nome_completo = ?, username = ?, senha = ?, perfil = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNomeCompleto());
            pstmt.setString(2, usuario.getUsername());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getPerfil());

            if (usuario.getId() != 0) {
                pstmt.setInt(5, usuario.getId());
            }

            pstmt.executeUpdate();
            System.out.println("Usuário salvo com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Outros métodos como buscarPorId, listarTodos, excluir podem ser adicionados aqui.
}
