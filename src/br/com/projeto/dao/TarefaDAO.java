package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Tarefa;
import br.com.projeto.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Busca todas as tarefas, incluindo os nomes do projeto e do responsável.
     * @return Uma lista de objetos Tarefa com seus dados associados.
     */
    public List<Tarefa> buscarTodas() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT t.id, t.descricao, t.status, " +
                     "p.id AS projeto_id, p.nome AS projeto_nome, " +
                     "u.id AS responsavel_id, u.nome_completo AS responsavel_nome " +
                     "FROM tarefas t " +
                     "LEFT JOIN projetos p ON t.projeto_id = p.id " +
                     "LEFT JOIN usuarios u ON t.responsavel_id = u.id " +
                     "ORDER BY t.id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setStatus(rs.getString("status"));

                // Associa o projeto
                if (rs.getObject("projeto_id") != null) {
                    Projeto projeto = new Projeto();
                    projeto.setId(rs.getInt("projeto_id"));
                    projeto.setNome(rs.getString("projeto_nome"));
                    tarefa.setProjeto(projeto);
                }

                // Associa o responsável
                if (rs.getObject("responsavel_id") != null) {
                    Usuario responsavel = new Usuario();
                    responsavel.setId(rs.getInt("responsavel_id"));
                    responsavel.setNomeCompleto(rs.getString("responsavel_nome"));
                    tarefa.setResponsavel(responsavel);
                }

                tarefas.add(tarefa);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar tarefas: " + e.getMessage());
            e.printStackTrace();
        }

        return tarefas;
    }
}
