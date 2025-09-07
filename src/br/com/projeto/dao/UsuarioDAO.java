package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.DesempenhoColaboradorDTO;
import br.com.projeto.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Busca todos os usuários cadastrados, exceto a senha.
     * @return Uma lista de objetos Usuario.
     */
    public List<Usuario> buscarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome_completo, username, perfil FROM usuarios ORDER BY nome_completo";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNomeCompleto(rs.getString("nome_completo"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPerfil(rs.getString("perfil"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Busca todos os usuários que podem ser gerentes de projeto (perfis 'gerente' ou 'administrador').
     * @return Uma lista de objetos Usuario que podem ser gerentes.
     */
    public List<Usuario> buscarGerentes() {
        List<Usuario> gerentes = new ArrayList<>();
        String sql = "SELECT id, nome_completo, username, perfil FROM usuarios WHERE perfil IN ('gerente', 'administrador') ORDER BY nome_completo";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNomeCompleto(rs.getString("nome_completo"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPerfil(rs.getString("perfil"));
                gerentes.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar gerentes: " + e.getMessage());
            e.printStackTrace();
        }
        return gerentes;
    }

    /**
     * Exclui um usuário do banco de dados pelo ID.
     * @param id O ID do usuário a ser excluído.
     */
    public void excluir(int id) {
        // Regra de segurança: impede a exclusão do usuário administrador principal (ID=1)
        if (id == 1) {
            System.err.println("Não é permitido excluir o administrador principal.");
            return;
        }

        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Usuário excluído com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gera um relatório de desempenho de todos os colaboradores.
     * @return Uma lista de DTOs com os dados de desempenho.
     */
    public List<DesempenhoColaboradorDTO> gerarRelatorioDesempenho() {
        List<DesempenhoColaboradorDTO> relatorio = new ArrayList<>();
        String sql = "SELECT " +
                     "    u.nome_completo, " +
                     "    COUNT(t.id) AS total_tarefas, " +
                     "    SUM(CASE WHEN t.status = 'Concluído' THEN 1 ELSE 0 END) AS tarefas_concluidas, " +
                     "    SUM(CASE WHEN t.status = 'Em Andamento' THEN 1 ELSE 0 END) AS tarefas_em_andamento " +
                     "FROM " +
                     "    usuarios u " +
                     "LEFT JOIN " +
                     "    tarefas t ON u.id = t.responsavel_id " +
                     "GROUP BY " +
                     "    u.id, u.nome_completo " +
                     "ORDER BY " +
                     "    u.nome_completo";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DesempenhoColaboradorDTO dto = new DesempenhoColaboradorDTO();
                dto.setNomeColaborador(rs.getString("nome_completo"));
                dto.setTotalTarefas(rs.getInt("total_tarefas"));
                dto.setTarefasConcluidas(rs.getInt("tarefas_concluidas"));
                dto.setTarefasEmAndamento(rs.getInt("tarefas_em_andamento"));
                relatorio.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório de desempenho: " + e.getMessage());
            e.printStackTrace();
        }

        return relatorio;
    }
}
