package br.com.projeto.database;

/**
 * Interface para centralizar as configurações de conexão com o banco de dados.
 * Ao usar uma interface, todas as variáveis são public, static e final por padrão.
 */
public interface DatabaseConfig {
    // --- CONFIGURAÇÕES GERAIS ---
    String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    String DB_NAME = "gestao_projetos_db";

    // --- URLs DE CONEXÃO ---
    String SERVER_URL = "jdbc:mysql://localhost:3306/";
    String CONNECTION_PARAMS = "?useTimezone=true&serverTimezone=UTC";

    // URL para conexão direta com o banco de dados da aplicação
    String DB_URL = SERVER_URL + DB_NAME + CONNECTION_PARAMS;

    // URL para conexão com o servidor (usada para verificar/criar o banco)
    String SERVER_CONNECTION_URL = SERVER_URL + CONNECTION_PARAMS;

    // --- CREDENCIAIS DE ACESSO ---
    String USER = "root";
    String PASSWORD = "your_password"; // <<-- COLOQUE SUA SENHA AQUI, APENAS NESTE LUGAR
}