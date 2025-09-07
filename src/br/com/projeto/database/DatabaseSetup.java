package br.com.projeto.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseSetup {

    public static void checkAndCreateDatabase() {
        try {
            // 1. Garante que o banco de dados exista
            ensureDatabaseExists();

            // 2. Executa o script para garantir que todas as tabelas estejam criadas/atualizadas
            executeSchemaScript();

        } catch (SQLException | IOException e) {
            System.err.println("ERRO CRÍTICO: Falha na configuração do banco de dados.");
            e.printStackTrace();
            throw new RuntimeException("Falha na configuração do banco de dados. Verifique se o MySQL está rodando.", e);
        }
    }

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.SERVER_CONNECTION_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            if (!databaseExists(conn)) {
                System.out.println("Banco de dados '" + DatabaseConfig.DB_NAME + "' não encontrado. Criando...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE " + DatabaseConfig.DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
                    System.out.println("Banco de dados '" + DatabaseConfig.DB_NAME + "' criado com sucesso.");
                }
            } else {
                System.out.println("Banco de dados '" + DatabaseConfig.DB_NAME + "' já existe.");
            }
        }
    }

    private static void executeSchemaScript() throws SQLException, IOException {
        System.out.println("Executando script de schema para garantir que as tabelas estejam atualizadas...");
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = conn.createStatement()) {

            try (InputStream inputStream = DatabaseSetup.class.getClassLoader().getResourceAsStream("schema.sql")) {
                if (inputStream == null) {
                    throw new IOException("Arquivo 'schema.sql' não encontrado no classpath.");
                }

                Scanner scanner = new Scanner(inputStream).useDelimiter(";");

                while (scanner.hasNext()) {
                    String sqlStatement = scanner.next().trim();
                    if (!sqlStatement.isEmpty()) {
                        stmt.execute(sqlStatement);
                    }
                }
            }
            System.out.println("Schema do banco de dados verificado e atualizado com sucesso.");
        } catch (SQLException | IOException e) {
            System.err.println("ERRO: Falha ao executar o script 'schema.sql'.");
            throw e;
        }
    }

    private static boolean databaseExists(Connection conn) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getCatalogs()) {
            while (rs.next()) {
                if (rs.getString(1).equalsIgnoreCase(DatabaseConfig.DB_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }
}
