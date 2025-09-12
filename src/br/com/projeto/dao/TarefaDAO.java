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
            ? "INSERT INTO tarefas (titulo, descricao, status, data_inicio_prevista, data_fim_prevista, data_inicio_real, data_fim_real, id_projeto, id_responsavel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            : "UPDATE tarefas SET titulo = ?, descricao = ?, status = ?, data_inicio_prevista = ?, data_fim_prevista = ?, data_inicio_real = ?, data_fim_real = ?, id_projeto = ?, id_responsavel = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tarefa.getTitulo());
            pstmt.setString(2, tarefa.getDescricao());
            pstmt.setString(3, tarefa.getStatus());

            // Set Date fields, handling nulls
            pstmt.setDate(4, tarefa.getDataInicioPrevista() != null ? new java.sql.Date(tarefa.getDataInicioPrevista().getTime()) : null);
            pstmt.setDate(5, tarefa.getDataFimPrevista() != null ? new java.sql.Date(tarefa.getDataFimPrevista().getTime()) : null);
            pstmt.setDate(6, tarefa.getDataInicioReal() != null ? new java.sql.Date(tarefa.getDataInicioReal().getTime()) : null);
            pstmt.setDate(7, tarefa.getDataFimReal() != null ? new java.sql.Date(tarefa.getDataFimReal().getTime()) : null);

            if (tarefa.getProjeto() != null) {
                pstmt.setInt(8, tarefa.getProjeto().getId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            if (tarefa.getResponsavel() != null) {
                pstmt.setInt(9, tarefa.getResponsavel().getId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }

            if (tarefa.getId() != 0) {
                pstmt.setInt(10, tarefa.getId());
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
        String sql = "SELECT t.*, " + // Select all columns from tarefas
                     "p.id AS projeto_id, p.nome_projeto AS projeto_nome, " +
                     "u.id AS responsavel_id, u.nome_completo AS responsavel_nome " +
                     "FROM tarefas t " +
                     "LEFT JOIN projetos p ON t.id_projeto = p.id " +
                     "LEFT JOIN usuarios u ON t.id_responsavel = u.id " +
                     "ORDER BY t.id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setDataInicioPrevista(rs.getDate("data_inicio_prevista"));
                tarefa.setDataFimPrevista(rs.getDate("data_fim_prevista"));
                tarefa.setDataInicioReal(rs.getDate("data_inicio_real"));
                tarefa.setDataFimReal(rs.getDate("data_fim_real"));

                // Associa o projeto
                if (rs.getObject("projeto_id") != null) {
                    Projeto projeto = new Projeto();
                    projeto.setId(rs.getInt("projeto_id"));
                    projeto.setNomeProjeto(rs.getString("projeto_nome"));
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

    /**
     * Exclui uma tarefa do banco de dados pelo ID.
     * @param id O ID da tarefa a ser excluída.
     */
    public void excluir(int id) {
        String sql = "DELETE FROM tarefas WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Tarefa excluída com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir tarefa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
