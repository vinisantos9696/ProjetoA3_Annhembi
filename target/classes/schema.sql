<<<<<<< HEAD
-- Garante uma base limpa, removendo tabelas antigas na ordem correta para evitar erros de FK.

-- 1. Remove as tabelas de associação que dependem de outras.
DROP TABLE IF EXISTS projeto_equipes;
DROP TABLE IF EXISTS equipe_projetos;
DROP TABLE IF EXISTS equipe_membros;

-- 2. Remove a tabela de tarefas, que depende de projetos e usuários.
DROP TABLE IF EXISTS tarefas;

-- 3. Agora que nenhuma tabela depende delas, remove as tabelas principais.
DROP TABLE IF EXISTS projetos;
DROP TABLE IF EXISTS equipes;
DROP TABLE IF EXISTS usuarios;

=======
>>>>>>> f183713ac417e0cd9647804be3d3d20d8605be58
-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL -- 'administrador', 'gerente', 'colaborador'
);

<<<<<<< HEAD
-- Tabela de Equipes
CREATE TABLE IF NOT EXISTS equipes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

=======
>>>>>>> f183713ac417e0cd9647804be3d3d20d8605be58
-- Tabela de Projetos
CREATE TABLE IF NOT EXISTS projetos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_inicio DATE,
    data_fim DATE,
    status VARCHAR(50),
    gerente_id INT,
    FOREIGN KEY (gerente_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Tabela de Tarefas
CREATE TABLE IF NOT EXISTS tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao TEXT NOT NULL,
    status VARCHAR(50),
    projeto_id INT,
    responsavel_id INT,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

<<<<<<< HEAD
=======
-- Tabela de Equipes
CREATE TABLE IF NOT EXISTS equipes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

>>>>>>> f183713ac417e0cd9647804be3d3d20d8605be58
-- Tabela de Associação: Equipe <-> Membros (Usuários)
CREATE TABLE IF NOT EXISTS equipe_membros (
    equipe_id INT,
    usuario_id INT,
    PRIMARY KEY (equipe_id, usuario_id),
    FOREIGN KEY (equipe_id) REFERENCES equipes(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela de Associação: Equipe <-> Projetos
CREATE TABLE IF NOT EXISTS equipe_projetos (
    equipe_id INT,
    projeto_id INT,
    PRIMARY KEY (equipe_id, projeto_id),
    FOREIGN KEY (equipe_id) REFERENCES equipes(id) ON DELETE CASCADE,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE
);

-- Inserir um usuário administrador padrão para o primeiro login, se não existir.
<<<<<<< HEAD
-- A sintaxe foi corrigida para evitar o erro 'Duplicate column name'.
INSERT INTO usuarios (nome_completo, username, senha, perfil)
SELECT col1, col2, col3, col4 FROM (SELECT 'Administrador' AS col1, 'admin' AS col2, 'admin' AS col3, 'administrador' AS col4) AS tmp
=======
INSERT INTO usuarios (nome_completo, username, senha, perfil)
SELECT * FROM (SELECT 'Administrador', 'admin', 'admin', 'administrador') AS tmp
>>>>>>> f183713ac417e0cd9647804be3d3d20d8605be58
WHERE NOT EXISTS (
    SELECT username FROM usuarios WHERE username = 'admin'
) LIMIT 1;
