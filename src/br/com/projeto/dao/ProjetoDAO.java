package br.com.projeto.dao;

import br.com.projeto.database.DatabaseConfig;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    /**
     * Salva um novo projeto ou atualiza um existente no banco de dados.
     * @param projeto O objeto Projeto a ser salvo.
     */
    public void salvar(Projeto projeto) {
        String sql = (projeto.getId() == 0)
            ? "INSERT INTO projetos (nome_projeto, descricao, data_inicio, data_fim_prevista, status, id_gerente) VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE projetos SET nome_projeto = ?, descricao = ?, data_inicio = ?, data_fim_prevista = ?, status = ?, id_gerente = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, projeto.getNomeProjeto());
            pstmt.setString(2, projeto.getDescricao());
            pstmt.setDate(3, new java.sql.Date(projeto.getDataInicio().getTime()));
            pstmt.setDate(4, new java.sql.Date(projeto.getDataFimPrevista().getTime()));
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

    /**
     * Busca todos os projetos e seus respectivos gerentes para exibição em relatórios.
     * @return Uma lista de objetos Projeto, cada um com seu gerente (se houver).
     */
    public List<Projeto> buscarTodosParaRelatorio() {
        List<Projeto> projetos = new ArrayList<>();
        // A consulta SQL une a tabela de projetos com a de usuários para buscar o nome do gerente.
        String sql = "SELECT p.*, u.nome_completo AS gerente_nome FROM projetos p LEFT JOIN usuarios u ON p.id_gerente = u.id ORDER BY p.nome_projeto";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNomeProjeto(rs.getString("nome_projeto"));
                projeto.setDescricao(rs.getString("descricao"));
                projeto.setDataInicio(rs.getDate("data_inicio"));
                projeto.setDataFimPrevista(rs.getDate("data_fim_prevista"));
                projeto.setStatus(rs.getString("status"));
                projeto.setIdGerente(rs.getInt("id_gerente"));

                // Se houver um gerente associado, cria o objeto Usuario e o associa ao projeto.
                if (rs.getInt("id_gerente") != 0) {
                    Usuario gerente = new Usuario();
                    gerente.setId(rs.getInt("id_gerente"));
                    gerente.setNomeCompleto(rs.getString("gerente_nome"));
                    projeto.setGerente(gerente);
                }

                projetos.add(projeto);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar projetos para relatório: " + e.getMessage());
            e.printStackTrace();
        }

        return projetos;
    }

    /**
     * Exclui um projeto do banco de dados pelo ID.
     * @param id O ID do projeto a ser excluído.
     */
    public void excluir(int id) {
        String sql = "DELETE FROM projetos WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Projeto excluído com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir projeto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
