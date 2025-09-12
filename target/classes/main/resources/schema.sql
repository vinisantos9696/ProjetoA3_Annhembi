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

-- Usuários (5 exemplos, incluindo os de teste)
INSERT INTO usuarios (id, nome_completo, email, login, senha, perfil) VALUES
(1, 'Administrador', 'admin@sistema.com', 'admin', 'admin', 'administrador'),
(2, 'Gerente', 'gerente@sistema.com', 'gerente', 'gerente', 'gerente'),
(3, 'Colaborador', 'colab@sistema.com', 'colab', 'colab', 'colaborador'),
(4, 'Beatriz Lima', 'beatriz.lima@sistema.com', 'beatriz', 'bia123', 'colaborador'),
(5, 'Davi Souza', 'davi.souza@sistema.com', 'davi', 'davi123', 'colaborador')
ON DUPLICATE KEY UPDATE nome_completo=VALUES(nome_completo), email=VALUES(email), login=VALUES(login), senha=VALUES(senha), perfil=VALUES(perfil);

-- Equipes (5 exemplos)
INSERT INTO equipes (id_equipe, nome_equipe, descricao) VALUES
(1, 'Equipe Alfa (Frontend)', 'Responsável pelo desenvolvimento da interface de usuário e experiência do cliente.'),
(2, 'Equipe Beta (Backend)', 'Responsável pela lógica de negócios, APIs e integração com o banco de dados.'),
(3, 'Equipe Gama (Mobile)', 'Focada no desenvolvimento de aplicativos para Android e iOS.'),
(4, 'Equipe Delta (QA e Testes)', 'Garante a qualidade e a estabilidade dos produtos através de testes automatizados e manuais.'),
(5, 'Equipe Ômega (DevOps)', 'Cuida da infraestrutura, automação de deploy e monitoramento dos sistemas.')
ON DUPLICATE KEY UPDATE nome_equipe=VALUES(nome_equipe), descricao=VALUES(descricao);

-- Membros das Equipes
INSERT INTO equipe_membros (id_equipe, id_usuario) VALUES
(1, 3),
(1, 4),
(2, 5),
(3, 3),
(4, 4)
ON DUPLICATE KEY UPDATE id_equipe=VALUES(id_equipe);

-- Projetos (5 exemplos)
INSERT INTO projetos (id, nome_projeto, descricao, data_inicio, data_fim_prevista, status, id_gerente) VALUES
(1, 'Sistema de E-commerce B2C', 'Plataforma completa de vendas online para o consumidor final.', '2024-01-10', '2024-09-30', 'em andamento', 2),
(2, 'Aplicativo de Gestão Financeira', 'App mobile para controle de despesas e receitas pessoais.', '2024-02-20', '2024-11-15', 'em andamento', 2),
(3, 'Modernização do Legado (ERP)', 'Refatoração e migração do sistema ERP interno para novas tecnologias.', '2024-03-01', '2025-06-30', 'planejado', 1),
(4, 'Plataforma de Análise de Dados', 'Ferramenta de BI para visualização de métricas de vendas.', '2024-04-15', '2024-12-20', 'planejado', 2),
(5, 'Website Institucional com CMS', 'Desenvolvimento do novo site da empresa com um sistema de gerenciamento de conteúdo.', '2024-05-05', '2024-08-25', 'concluído', 1)
ON DUPLICATE KEY UPDATE nome_projeto=VALUES(nome_projeto), descricao=VALUES(descricao), status=VALUES(status), id_gerente=VALUES(id_gerente);

-- Associações de Equipes aos Projetos
INSERT INTO projeto_equipes (id_projeto, id_equipe) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 2),
(3, 5),
(5, 1)
ON DUPLICATE KEY UPDATE id_projeto=VALUES(id_projeto);

-- Tarefas (5 exemplos)
INSERT INTO tarefas (id, titulo, descricao, id_projeto, id_responsavel, status, data_inicio, data_fim_prevista) VALUES
(1, 'Desenvolver Carrinho de Compras', 'Implementar a funcionalidade completa do carrinho de compras no e-commerce.', 1, 3, 'em execução', '2024-05-10', '2024-06-10'),
(2, 'Criar Tela de Login do App', 'Desenvolver a interface e a lógica de autenticação para o aplicativo financeiro.', 2, 3, 'pendente', '2024-05-15', '2024-06-05'),
(3, 'Configurar Pipeline de CI/CD', 'Automatizar o processo de build e deploy para o projeto de modernização do ERP.', 3, 5, 'em execução', '2024-04-25', '2024-05-30'),
(4, 'Testar API de Pagamentos', 'Criar e executar testes de integração para a API de pagamentos do e-commerce.', 1, 4, 'pendente', '2024-05-20', '2024-06-20'),
(5, 'Migrar Banco de Dados de Clientes', 'Criar script para migrar os dados de clientes do sistema legado para o novo ERP.', 3, 5, 'concluída', '2024-04-01', '2024-04-30')
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo), descricao=VALUES(descricao), status=VALUES(status), id_responsavel=VALUES(id_responsavel);
