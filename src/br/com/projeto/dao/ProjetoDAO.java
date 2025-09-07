package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import java.sql.*;

public class ProjetoDAO {

    /**
     * Salva um novo projeto ou atualiza um existente no banco de dados.
     * @param projeto O objeto Projeto a ser salvo.
     */
    public void salvar(Projeto projeto) {
        String sql = (projeto.getId() == 0)
            ? "INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, gerente_id) VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE projetos SET nome = ?, descricao = ?, data_inicio = ?, data_fim = ?, status = ?, gerente_id = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, projeto.getNome());
            pstmt.setString(2, projeto.getDescricao());
            pstmt.setDate(3, new java.sql.Date(projeto.getDataInicio().getTime()));
            pstmt.setDate(4, new java.sql.Date(projeto.getDataFim().getTime()));
            pstmt.setString(5, projeto.getStatus());
            
            if (projeto.getGerente() != null) {
                pstmt.setInt(6, projeto.getGerente().getId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            if (projeto.getId() != 0) {
                pstmt.setInt(7, projeto.getId());
            }

            pstmt.executeUpdate();
            System.out.println("Projeto salvo com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar projeto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Outros métodos como buscarPorId, listarTodos, excluir podem ser adicionados aqui.
}
