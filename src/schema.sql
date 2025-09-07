-- Garante uma base limpa, removendo tabelas antigas na ordem correta para evitar erros de FK.

-- 1. Remove as tabelas de associação que dependem de outras.
DROP TABLE IF EXISTS projeto_equipes; -- Tabela antiga, mantida para limpeza
DROP TABLE IF EXISTS equipe_projetos;
DROP TABLE IF EXISTS equipe_membros;

-- 2. Remove a tabela de tarefas, que depende de projetos e usuários.
DROP TABLE IF EXISTS tarefas;

-- 3. Agora que nenhuma tabela depende delas, remove as tabelas principais.
DROP TABLE IF EXISTS projetos;
DROP TABLE IF EXISTS equipes;
DROP TABLE IF EXISTS usuarios;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL -- 'administrador', 'gerente', 'colaborador'
);

-- Tabela de Equipes
CREATE TABLE IF NOT EXISTS equipes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

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

-- Inserir usuários padrão para o primeiro login, se não existirem.

-- Usuário Administrador
INSERT INTO usuarios (nome_completo, username, senha, perfil)
SELECT col1, col2, col3, col4 FROM (SELECT 'Administrador' AS col1, 'admin' AS col2, 'admin' AS col3, 'administrador' AS col4) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM usuarios WHERE username = 'admin'
) LIMIT 1;

-- Usuário Gerente de Teste
INSERT INTO usuarios (nome_completo, username, senha, perfil)
SELECT col1, col2, col3, col4 FROM (SELECT 'Gerente de Teste' AS col1, 'gerente' AS col2, 'gerente' AS col3, 'gerente' AS col4) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM usuarios WHERE username = 'gerente'
) LIMIT 1;

-- Usuário Colaborador de Teste
INSERT INTO usuarios (nome_completo, username, senha, perfil)
SELECT col1, col2, col3, col4 FROM (SELECT 'Colaborador de Teste' AS col1, 'colab' AS col2, 'colab' AS col3, 'colaborador' AS col4) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM usuarios WHERE username = 'colab'
) LIMIT 1;

-- ##################################################################
-- ## DADOS DE EXEMPLO PARA TESTES ##
-- ##################################################################

-- Inserir Projetos de Exemplo (assumindo que o gerente 'gerente' tem ID 2)
INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, gerente_id)
SELECT * FROM (SELECT 'Desenvolvimento do Novo App Mobile', 'Criar um novo aplicativo para iOS e Android.', '2024-01-15', '2024-08-30', 'Em Andamento', 2) AS tmp
WHERE NOT EXISTS (SELECT nome FROM projetos WHERE nome = 'Desenvolvimento do Novo App Mobile');

INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, gerente_id)
SELECT * FROM (SELECT 'Migração de Servidores para a Nuvem', 'Mover toda a infraestrutura on-premise para a AWS.', '2024-03-01', '2024-12-01', 'Planejado', 2) AS tmp
WHERE NOT EXISTS (SELECT nome FROM projetos WHERE nome = 'Migração de Servidores para a Nuvem');

INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, gerente_id)
SELECT * FROM (SELECT 'Website Corporativo - Redesign', 'Atualizar o design e conteúdo do site principal da empresa.', '2023-11-10', '2024-04-20', 'Concluído', 2) AS tmp
WHERE NOT EXISTS (SELECT nome FROM projetos WHERE nome = 'Website Corporativo - Redesign');

-- Inserir Tarefas de Exemplo (assumindo que os projetos e usuários existem com os IDs corretos)
INSERT INTO tarefas (descricao, status, projeto_id, responsavel_id)
SELECT * FROM (SELECT 'Desenhar mockups da interface do app', 'Concluído', 1, 3) AS tmp
WHERE NOT EXISTS (SELECT id FROM tarefas WHERE descricao = 'Desenhar mockups da interface do app' AND projeto_id = 1);

INSERT INTO tarefas (descricao, status, projeto_id, responsavel_id)
SELECT * FROM (SELECT 'Desenvolver a API de autenticação', 'Em Andamento', 1, 3) AS tmp
WHERE NOT EXISTS (SELECT id FROM tarefas WHERE descricao = 'Desenvolver a API de autenticação' AND projeto_id = 1);

INSERT INTO tarefas (descricao, status, projeto_id, responsavel_id)
SELECT * FROM (SELECT 'Analisar provedores de nuvem (AWS, Azure, Google Cloud)', 'Planejado', 2, 3) AS tmp
WHERE NOT EXISTS (SELECT id FROM tarefas WHERE descricao = 'Analisar provedores de nuvem (AWS, Azure, Google Cloud)' AND projeto_id = 2);

-- Inserir Equipes de Exemplo
INSERT INTO equipes (nome)
SELECT 'Equipe Alpha (Mobile)' FROM DUAL WHERE NOT EXISTS (SELECT nome FROM equipes WHERE nome = 'Equipe Alpha (Mobile)');

INSERT INTO equipes (nome)
SELECT 'Equipe Bravo (Infra)' FROM DUAL WHERE NOT EXISTS (SELECT nome FROM equipes WHERE nome = 'Equipe Bravo (Infra)');

INSERT INTO equipes (nome)
SELECT 'Equipe Charlie (Web)' FROM DUAL WHERE NOT EXISTS (SELECT nome FROM equipes WHERE nome = 'Equipe Charlie (Web)');

-- Associar Membros à Equipe (assumindo que as equipes e usuários têm os IDs corretos)
INSERT INTO equipe_membros (equipe_id, usuario_id)
SELECT 1, 3 FROM DUAL WHERE NOT EXISTS (SELECT equipe_id, usuario_id FROM equipe_membros WHERE equipe_id = 1 AND usuario_id = 3);

INSERT INTO equipe_membros (equipe_id, usuario_id)
SELECT 2, 2 FROM DUAL WHERE NOT EXISTS (SELECT equipe_id, usuario_id FROM equipe_membros WHERE equipe_id = 2 AND usuario_id = 2);

-- Associar Equipes a Projetos
INSERT INTO equipe_projetos (equipe_id, projeto_id)
SELECT 1, 1 FROM DUAL WHERE NOT EXISTS (SELECT equipe_id, projeto_id FROM equipe_projetos WHERE equipe_id = 1 AND projeto_id = 1);

INSERT INTO equipe_projetos (equipe_id, projeto_id)
SELECT 2, 2 FROM DUAL WHERE NOT EXISTS (SELECT equipe_id, projeto_id FROM equipe_projetos WHERE equipe_id = 2 AND projeto_id = 2);
