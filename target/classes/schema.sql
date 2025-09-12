-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('administrador', 'gerente', 'colaborador') NOT NULL
);

-- Tabela de Equipes
CREATE TABLE IF NOT EXISTS equipes (
    id_equipe INT AUTO_INCREMENT PRIMARY KEY,
    nome_equipe VARCHAR(100) NOT NULL,
    descricao TEXT
);

-- Tabela de Projetos
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

-- Inserção de Dados de Exemplo --

-- Usuários
INSERT INTO usuarios (id, nome_completo, email, login, senha, perfil) VALUES
(1, 'Administrador do Sistema', 'admin@sistema.com', 'admin', 'admin', 'administrador'),
(2, 'Gerente de Projetos', 'gerente@sistema.com', 'gerente', 'gerente', 'gerente'),
(3, 'Ana Silva', 'ana.silva@sistema.com', 'ana', 'ana123', 'colaborador'),
(4, 'Bruno Costa', 'bruno.costa@sistema.com', 'bruno', 'bruno123', 'colaborador'),
(5, 'Carlos Dias', 'carlos.dias@sistema.com', 'carlos', 'carlos123', 'colaborador')
ON DUPLICATE KEY UPDATE nome_completo=VALUES(nome_completo);

-- Equipes
INSERT INTO equipes (id_equipe, nome_equipe, descricao) VALUES
(1, 'Equipe de Desenvolvimento Frontend', 'Responsável pela interface do usuário'),
(2, 'Equipe de Desenvolvimento Backend', 'Responsável pela lógica de negócios e APIs'),
(3, 'Equipe de Qualidade e Testes', 'Responsável por garantir a qualidade do software')
ON DUPLICATE KEY UPDATE nome_equipe=VALUES(nome_equipe);

-- Membros das Equipes
INSERT INTO equipe_membros (id_equipe, id_usuario) VALUES
(1, 3), -- Ana no Frontend
(2, 4), -- Bruno no Backend
(3, 5)  -- Carlos em QA
ON DUPLICATE KEY UPDATE id_equipe=VALUES(id_equipe);

-- Projetos
INSERT INTO projetos (id, nome_projeto, descricao, data_inicio, data_fim_prevista, status, id_gerente) VALUES
(1, 'Desenvolvimento do Novo Portal Corporativo', 'Criar um novo portal para a empresa com design moderno e responsivo.', '2024-01-15', '2024-08-30', 'em andamento', 2),
(2, 'Migração de Servidores para a Nuvem', 'Mover toda a infraestrutura de servidores on-premise para a AWS.', '2024-03-01', '2024-12-31', 'planejado', 2)
ON DUPLICATE KEY UPDATE nome_projeto=VALUES(nome_projeto);

-- Associações de Equipes aos Projetos
INSERT INTO projeto_equipes (id_projeto, id_equipe) VALUES
(1, 1), -- Frontend no projeto do portal
(1, 2)  -- Backend no projeto do portal
ON DUPLICATE KEY UPDATE id_projeto=VALUES(id_projeto);

-- Tarefas
INSERT INTO tarefas (id, titulo, descricao, id_projeto, id_responsavel, status, data_inicio, data_fim_prevista) VALUES
(1, 'Criar layout da página inicial', 'Desenvolver o HTML e CSS da home page do novo portal.', 1, 3, 'em execução', '2024-02-01', '2024-02-28'),
(2, 'Configurar API de autenticação', 'Implementar o endpoint de login/logout usando JWT.', 1, 4, 'pendente', '2024-02-10', '2024-03-10'),
(3, 'Elaborar plano de testes', 'Criar o plano de testes para o projeto do portal.', 1, 5, 'pendente', '2024-02-15', '2024-03-15'),
(4, 'Levantamento de requisitos da migração', 'Detalhar todos os serviços e dependências para a migração para a nuvem.', 2, 2, 'pendente', '2024-03-05', '2024-03-25')
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo);
