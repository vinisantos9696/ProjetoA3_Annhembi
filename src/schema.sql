-- Tabela de Usuários
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL
);

-- Tabela de Equipes
CREATE TABLE equipes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

-- Tabela de Projetos
CREATE TABLE projetos (
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
CREATE TABLE tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao TEXT NOT NULL,
    status VARCHAR(50),
    projeto_id INT,
    responsavel_id INT,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Tabela de Associação: Equipe <-> Membros (Usuários)
CREATE TABLE equipe_membros (
    equipe_id INT,
    usuario_id INT,
    PRIMARY KEY (equipe_id, usuario_id),
    FOREIGN KEY (equipe_id) REFERENCES equipes(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela de Associação: Equipe <-> Projetos
CREATE TABLE equipe_projetos (
    equipe_id INT,
    projeto_id INT,
    PRIMARY KEY (equipe_id, projeto_id),
    FOREIGN KEY (equipe_id) REFERENCES equipes(id) ON DELETE CASCADE,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE
);

-- ##################################################################
-- ## DADOS DE EXEMPLO PARA TESTES ##
-- ##################################################################

-- Usuários (IDs: 1=admin, 2=gerente, 3=colab)
INSERT INTO usuarios (nome_completo, username, senha, perfil) VALUES
('Administrador', 'admin', 'admin', 'administrador'),
('Gerente de Teste', 'gerente', 'gerente', 'gerente'),
('Colaborador de Teste', 'colab', 'colab', 'colaborador');

-- Equipes (IDs: 1=Alpha, 2=Bravo, 3=Charlie)
INSERT INTO equipes (nome) VALUES
('Equipe Alpha (Mobile)'),
('Equipe Bravo (Infra)'),
('Equipe Charlie (Web)');

-- Projetos (IDs: 1=Mobile, 2=Nuvem, 3=Website)
INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, gerente_id) VALUES
('Desenvolvimento do Novo App Mobile', 'Criar um novo aplicativo para iOS e Android.', '2024-01-15', '2024-08-30', 'Em Andamento', 2),
('Migração de Servidores para a Nuvem', 'Mover toda a infraestrutura on-premise para a AWS.', '2024-03-01', '2024-12-01', 'Planejado', 2),
('Website Corporativo - Redesign', 'Atualizar o design e conteúdo do site principal da empresa.', '2023-11-10', '2024-04-20', 'Concluído', 2);

-- Tarefas
INSERT INTO tarefas (descricao, status, projeto_id, responsavel_id) VALUES
('Desenhar mockups da interface do app', 'Concluído', 1, 3),
('Desenvolver a API de autenticação', 'Em Andamento', 1, 3),
('Analisar provedores de nuvem (AWS, Azure, Google Cloud)', 'Planejado', 2, 3);

-- Associação de Membros e Equipes
INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES
(1, 3),
(2, 2);

-- Associação de Equipes e Projetos
INSERT INTO equipe_projetos (equipe_id, projeto_id) VALUES
(1, 1),
(2, 2);
