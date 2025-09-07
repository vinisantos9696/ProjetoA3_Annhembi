package br.com.projeto.database;

/**
 * Classe para centralizar as configurações de conexão com o banco de dados.
 * As credenciais (usuário e senha) são lidas de variáveis de ambiente.
 */
public final class DatabaseConfig {

    private DatabaseConfig() {
        // Impede a instanciação da classe
    }

    // --- CONFIGURAÇÕES GERAIS ---
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_NAME = "gestao_projetos_db";

    // --- URLs DE CONEXÃO ---
    public static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    public static final String CONNECTION_PARAMS = "?useTimezone=true&serverTimezone=UTC";

    // URL para conexão direta com o banco de dados da aplicação
    public static final String DB_URL = SERVER_URL + DB_NAME + CONNECTION_PARAMS;

    // URL para conexão com o servidor (usada para verificar/criar o banco)
    public static final String SERVER_CONNECTION_URL = SERVER_URL + CONNECTION_PARAMS;

    // --- CREDENCIAIS DE ACESSO ---
    // As credenciais são lidas de variáveis de ambiente para segurança.
    public static final String USER = getEnvVariable("DB_USER");
    public static final String PASSWORD = getEnvVariable("DB_PASSWORD");

    /**
     * Método auxiliar para ler variáveis de ambiente e lançar um erro se não estiverem definidas.
     * Isso garante que a aplicação não execute com configuração faltando.
     *
     * @param varName O nome da variável de ambiente.
     * @return O valor da variável de ambiente.
     * @throws IllegalStateException Se a variável de ambiente não estiver definida.
     */
    private static String getEnvVariable(String varName) {
        String value = System.getenv(varName);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                "A variável de ambiente obrigatória '" + varName + "' não está definida.\n" +
                "Configure-a nas variáveis de ambiente do seu sistema ou na configuração de execução da sua IDE."
            );
        }
        return value;
    }
}
