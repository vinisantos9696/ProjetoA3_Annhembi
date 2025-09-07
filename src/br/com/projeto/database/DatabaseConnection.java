package br.com.projeto.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados MySQL.
 * Utiliza o padrão Singleton para garantir uma única instância de conexão.
 */
public class DatabaseConnection {

    private static Connection connection = null;

    /**
     * Obtém uma conexão com o banco de dados.
     * Se a conexão ainda não foi estabelecida, uma nova é criada.
     *
     * @return Uma instância de Connection.
     * @throws SQLException se ocorrer um erro ao conectar.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Carrega o driver JDBC do MySQL
                Class.forName(DatabaseConfig.DB_DRIVER);
                // Utiliza as constantes da interface de configuração
                connection = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            // Lança uma SQLException se o driver não for encontrado
            throw new SQLException("Driver JDBC do MySQL não encontrado.", e);
        }
        return connection;
    }
}