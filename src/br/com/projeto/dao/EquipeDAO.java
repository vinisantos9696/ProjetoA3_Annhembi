package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Equipe;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    /**
     * Salva uma nova equipe ou atualiza uma existente, incluindo seus membros e projetos.
     * @param equipe O objeto Equipe a ser salvo.
     */
    public void salvar(Equipe equipe) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
            conn.setAutoCommit(false); // Inicia a transação

            // 1. Salva ou atualiza a equipe na tabela 'equipes'
            salvarEquipePrincipal(conn, equipe);

            // 2. Atualiza a tabela de associação 'equipe_membros'
            atualizarMembros(conn, equipe);

            // 3. Atualiza a tabela de associação 'equipe_projetos'
            atualizarProjetos(conn, equipe);

            conn.commit(); // Confirma a transação
            System.out.println("Equipe salva com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar equipe: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz a transação em caso de erro
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void salvarEquipePrincipal(Connection conn, Equipe equipe) throws SQLException {
        String sql = (equipe.getId() == 0)
            ? "INSERT INTO equipes (nome) VALUES (?)"
            : "UPDATE equipes SET nome = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, equipe.getNome());
            if (equipe.getId() != 0) {
                pstmt.setInt(2, equipe.getId());
            }
            pstmt.executeUpdate();

            // Se for um novo registro, pega o ID gerado e atualiza o objeto
            if (equipe.getId() == 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        equipe.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    private void atualizarMembros(Connection conn, Equipe equipe) throws SQLException {
        // 1. Remove todas as associações de membros existentes para esta equipe
        String deleteSql = "DELETE FROM equipe_membros WHERE equipe_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, equipe.getId());
            pstmt.executeUpdate();
        }

        // 2. Insere as novas associações de membros
        if (equipe.getMembros() != null && !equipe.getMembros().isEmpty()) {
            String insertSql = "INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (Usuario membro : equipe.getMembros()) {
                    pstmt.setInt(1, equipe.getId());
                    pstmt.setInt(2, membro.getId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }

    private void atualizarProjetos(Connection conn, Equipe equipe) throws SQLException {
        // 1. Remove todas as associações de projetos existentes para esta equipe
        String deleteSql = "DELETE FROM equipe_projetos WHERE equipe_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, equipe.getId());
            pstmt.executeUpdate();
        }

        // 2. Insere as novas associações de projetos
        if (equipe.getProjetosAlocados() != null && !equipe.getProjetosAlocados().isEmpty()) {
            String insertSql = "INSERT INTO equipe_projetos (equipe_id, projeto_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (Projeto projeto : equipe.getProjetosAlocados()) {
                    pstmt.setInt(1, equipe.getId());
                    pstmt.setInt(2, projeto.getId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }

    /**
     * Busca todas as equipes cadastradas no banco de dados.
     * @return Uma lista de objetos Equipe.
     */
    public List<Equipe> buscarTodas() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT id, nome FROM equipes ORDER BY nome";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNome(rs.getString("nome"));
                equipes.add(equipe);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar equipes: " + e.getMessage());
            e.printStackTrace();
        }

        return equipes;
    }

    /**
     * Exclui uma equipe do banco de dados pelo ID.
     * @param id O ID da equipe a ser excluída.
     */
    public void excluir(int id) {
        String sql = "DELETE FROM equipes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Equipe excluída com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir equipe: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
