package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Tarefa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TarefaDAO {

    /**
     * Salva uma nova tarefa ou atualiza uma existente no banco de dados.
     * @param tarefa O objeto Tarefa a ser salvo.
     */
    public void salvar(Tarefa tarefa) {
        String sql = (tarefa.getId() == 0)
            ? "INSERT INTO tarefas (descricao, status, projeto_id, responsavel_id) VALUES (?, ?, ?, ?)"
            : "UPDATE tarefas SET descricao = ?, status = ?, projeto_id = ?, responsavel_id = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tarefa.getDescricao());
            pstmt.setString(2, tarefa.getStatus());

            if (tarefa.getProjeto() != null) {
                pstmt.setInt(3, tarefa.getProjeto().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (tarefa.getResponsavel() != null) {
                pstmt.setInt(4, tarefa.getResponsavel().getId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            if (tarefa.getId() != 0) {
                pstmt.setInt(5, tarefa.getId());
            }

            pstmt.executeUpdate();
            System.out.println("Tarefa salva com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar tarefa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Outros métodos como buscarPorId, listarPorProjeto, etc., podem ser adicionados aqui.
}
