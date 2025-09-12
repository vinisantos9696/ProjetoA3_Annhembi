package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Equipe;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    public void salvar(Equipe equipe) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
            conn.setAutoCommit(false);

            salvarEquipePrincipal(conn, equipe);
            atualizarMembros(conn, equipe);
            atualizarProjetos(conn, equipe);

            conn.commit();
            System.out.println("Equipe salva com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar equipe: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
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
        String sql = (equipe.getIdEquipe() == 0)
            ? "INSERT INTO equipes (nome_equipe, descricao) VALUES (?, ?)"
            : "UPDATE equipes SET nome_equipe = ?, descricao = ? WHERE id_equipe = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, equipe.getNomeEquipe());
            pstmt.setString(2, equipe.getDescricao());
            if (equipe.getIdEquipe() != 0) {
                pstmt.setInt(3, equipe.getIdEquipe());
            }
            pstmt.executeUpdate();

            if (equipe.getIdEquipe() == 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        equipe.setIdEquipe(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    private void atualizarMembros(Connection conn, Equipe equipe) throws SQLException {
        String deleteSql = "DELETE FROM equipe_membros WHERE id_equipe = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, equipe.getIdEquipe());
            pstmt.executeUpdate();
        }

        if (equipe.getMembros() != null && !equipe.getMembros().isEmpty()) {
            String insertSql = "INSERT INTO equipe_membros (id_equipe, id_usuario) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (Usuario membro : equipe.getMembros()) {
                    pstmt.setInt(1, equipe.getIdEquipe());
                    pstmt.setInt(2, membro.getId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }

    private void atualizarProjetos(Connection conn, Equipe equipe) throws SQLException {
        String deleteSql = "DELETE FROM projeto_equipes WHERE id_equipe = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, equipe.getIdEquipe());
            pstmt.executeUpdate();
        }

        if (equipe.getProjetosAlocados() != null && !equipe.getProjetosAlocados().isEmpty()) {
            String insertSql = "INSERT INTO projeto_equipes (id_equipe, id_projeto) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (Projeto projeto : equipe.getProjetosAlocados()) {
                    pstmt.setInt(1, equipe.getIdEquipe());
                    pstmt.setInt(2, projeto.getId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }

    public List<Equipe> buscarTodas() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT id_equipe, nome_equipe, descricao FROM equipes ORDER BY nome_equipe";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setIdEquipe(rs.getInt("id_equipe"));
                equipe.setNomeEquipe(rs.getString("nome_equipe"));
                equipe.setDescricao(rs.getString("descricao"));
                equipes.add(equipe);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar equipes: " + e.getMessage());
            e.printStackTrace();
        }

        return equipes;
    }

    public Equipe buscarPorId(int idEquipe) {
        Equipe equipe = null;
        String equipeSql = "SELECT id_equipe, nome_equipe, descricao FROM equipes WHERE id_equipe = ?";
        String membrosSql = "SELECT u.id, u.nome_completo, u.login, u.perfil FROM usuarios u " +
                            "JOIN equipe_membros em ON u.id = em.id_usuario WHERE em.id_equipe = ?";
        String projetosSql = "SELECT p.id, p.nome_projeto FROM projetos p " +
                             "JOIN projeto_equipes pe ON p.id = pe.id_projeto WHERE pe.id_equipe = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            // 1. Buscar a equipe principal
            try (PreparedStatement pstmt = conn.prepareStatement(equipeSql)) {
                pstmt.setInt(1, idEquipe);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        equipe = new Equipe();
                        equipe.setIdEquipe(rs.getInt("id_equipe"));
                        equipe.setNomeEquipe(rs.getString("nome_equipe"));
                        equipe.setDescricao(rs.getString("descricao"));
                    }
                }
            }

            if (equipe != null) {
                // 2. Buscar os membros da equipe
                List<Usuario> membros = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(membrosSql)) {
                    pstmt.setInt(1, idEquipe);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Usuario membro = new Usuario();
                            membro.setId(rs.getInt("id"));
                            membro.setNomeCompleto(rs.getString("nome_completo"));
                            membro.setLogin(rs.getString("login"));
                            membro.setPerfil(rs.getString("perfil"));
                            membros.add(membro);
                        }
                    }
                }
                equipe.setMembros(membros);

                // 3. Buscar os projetos alocados
                List<Projeto> projetos = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(projetosSql)) {
                    pstmt.setInt(1, idEquipe);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Projeto projeto = new Projeto();
                            projeto.setId(rs.getInt("id"));
                            projeto.setNomeProjeto(rs.getString("nome_projeto"));
                            projetos.add(projeto);
                        }
                    }
                }
                equipe.setProjetosAlocados(projetos);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar equipe por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return equipe;
    }

    public void excluir(int idEquipe) {
        String sql = "DELETE FROM equipes WHERE id_equipe = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEquipe);
            pstmt.executeUpdate();
            System.out.println("Equipe excluída com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir equipe: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
