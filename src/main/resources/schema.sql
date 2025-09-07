-- Tabela de Usuários
-- Armazena informações sobre todos os usuários do sistema.
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL, -- Em um sistema real, armazene senhas com hash
    perfil ENUM('administrador', 'gerente', 'colaborador') NOT NULL
);

-- Tabela de Equipes
-- Armazena informações sobre as equipes de trabalho.
CREATE TABLE IF NOT EXISTS equipes (
    id_equipe INT AUTO_INCREMENT PRIMARY KEY,
    nome_equipe VARCHAR(100) NOT NULL,
    descricao TEXT
);

-- Tabela de Projetos
-- Armazena os detalhes dos projetos. O id_gerente é uma chave estrangeira para a tabela de usuários.
CREATE TABLE IF NOT EXISTS projetos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_projeto VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_inicio DATE,
    data_fim_prevista DATE,
    status ENUM('planejado', 'em andamento', 'concluído', 'cancelado') NOT NULL,
    id_gerente INT,
    FOREIGN KEY (id_gerente) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Tabela de Tarefas
-- Armazena as tarefas associadas a um projeto e a um responsável.
CREATE TABLE IF NOT EXISTS tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    id_projeto INT NOT NULL,
    id_responsavel INT,
    status ENUM('pendente', 'em execução', 'concluída') NOT NULL,
    data_inicio DATE,
    data_fim_prevista DATE,
    data_fim_real DATE,
    FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_responsavel) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Tabela de Associação: Membros da Equipe
CREATE TABLE IF NOT EXISTS equipe_membros (
    id_equipe INT NOT NULL,
    id_usuario INT NOT NULL,
    PRIMARY KEY (id_equipe, id_usuario),
    FOREIGN KEY (id_equipe) REFERENCES equipes(id_equipe) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela de Associação: Equipes do Projeto
CREATE TABLE IF NOT EXISTS projeto_equipes (
    id_projeto INT NOT NULL,
    id_equipe INT NOT NULL,
    PRIMARY KEY (id_projeto, id_equipe),
    FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_equipe) REFERENCES equipes(id_equipe) ON DELETE CASCADE
);

-- Inserindo um usuário administrador padrão para o primeiro acesso
INSERT INTO usuarios (nome_completo, email, login, senha, perfil)
VALUES ('Administrador do Sistema', 'admin@sistema.com', 'admin', 'admin', 'administrador')
ON DUPLICATE KEY UPDATE nome_completo = 'Administrador do Sistema';