package br.com.projeto.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    /**
     * Ponto de entrada para verificar e configurar o banco de dados de forma persistente.
     * Se o banco de dados não existir, ele é criado e populado com dados iniciais.
     * Se já existir, a aplicação apenas se conecta a ele, preservando os dados.
     */
    public static void setupDatabase() {
        try {
            // Conecta-se ao servidor MySQL para verificar a existência do banco de dados
            try (Connection serverConn = DriverManager.getConnection(DatabaseConfig.SERVER_CONNECTION_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
                
                if (!databaseExists(serverConn)) {
                    // --- EXECUÇÃO DA PRIMEIRA VEZ ---
                    System.out.println("Banco de dados '" + DatabaseConfig.DB_NAME + "' não encontrado. Configurando pela primeira vez...");
                    
                    // 1. Cria o banco de dados
                    try (Statement stmt = serverConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE " + DatabaseConfig.DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
                        System.out.println(" -> Banco de dados criado com sucesso.");
                    }
                    
                    // 2. Executa o script para criar tabelas e inserir dados iniciais
                    executeSchemaScript();

                } else {
                    // --- EXECUÇÕES SUBSEQUENTES ---
                    System.out.println("Banco de dados '" + DatabaseConfig.DB_NAME + "' já existe. Inicialização normal.");
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("ERRO CRÍTICO: Falha na configuração do banco de dados.");
            e.printStackTrace();
            throw new RuntimeException("Falha na configuração do banco de dados. Verifique se o MySQL está rodando.", e);
        }
    }

    /**
     * Verifica se o banco de dados da aplicação já existe no servidor MySQL.
     * @param conn Conexão com o servidor MySQL (não com o banco de dados específico).
     * @return true se o banco de dados existir, false caso contrário.
     * @throws SQLException Se ocorrer um erro de SQL.
     */
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

    /**
     * Executa o script 'schema.sql' para criar a estrutura de tabelas e inserir dados iniciais.
     * Este método deve ser chamado apenas quando o banco de dados é criado pela primeira vez.
     */
    private static void executeSchemaScript() throws SQLException, IOException {
        System.out.println(" -> Executando script 'schema.sql' para criar tabelas e dados iniciais...");
        try (Connection dbConn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = dbConn.createStatement()) {

            try (InputStream inputStream = DatabaseSetup.class.getClassLoader().getResourceAsStream("schema.sql")) {
                if (inputStream == null) {
                    throw new IOException("Arquivo 'schema.sql' não encontrado no classpath.");
                }

                // Leitura do script para uma única string
                String script;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    script = sb.toString();
                }

                // Divide o script em instruções individuais pelo ponto e vírgula
                String[] individualStatements = script.split(";");

                for (String statement : individualStatements) {
                    // Remove comentários e apara espaços
                    String cleanedStatement = statement.replaceAll("--.*", "").replaceAll("/\\*[\\s\\S]*?\\*/", "").trim();
                    
                    // Executa a instrução se não estiver vazia
                    if (!cleanedStatement.isEmpty()) {
                        stmt.execute(cleanedStatement);
                    }
                }
            }
            System.out.println(" -> Script 'schema.sql' executado com sucesso. Banco de dados pronto para uso.");
        } catch (SQLException | IOException e) {
            System.err.println("ERRO: Falha ao executar o script 'schema.sql'.");
            throw e;
        }
    }
}
