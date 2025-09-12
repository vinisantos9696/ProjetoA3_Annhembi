-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(255) UNIQUE,
    cargo VARCHAR(100),
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
    data_inicio_prevista DATE,
    data_fim_prevista DATE,
    data_inicio_real DATE,
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

-- Usuários (com novos campos)
INSERT INTO usuarios (id, nome_completo, cpf, email, cargo, login, senha, perfil) VALUES
(1, 'Administrador', '000.000.000-00', 'admin@empresa.com', 'Administrador de Sistemas', 'admin', 'admin', 'administrador'),
(2, 'Gerente', '111.111.111-11', 'gerente@empresa.com', 'Gerente de Projetos', 'gerente', 'gerente', 'gerente'),
(3, 'Colaborador', '222.222.222-22', 'colaborador@empresa.com', 'Desenvolvedor Pleno', 'colab', 'colab', 'colaborador'),
(4, 'Beatriz Lima', '333.333.333-33', 'beatriz.lima@empresa.com', 'Desenvolvedora Frontend', 'beatriz', 'bia123', 'colaborador'),
(5, 'Davi Souza', '444.444.444-44', 'davi.souza@empresa.com', 'Desenvolvedor Backend', 'davi', 'davi123', 'colaborador'),
(6, 'Fernanda Costa', '555.555.555-55', 'fernanda.costa@empresa.com', 'Gerente de Projetos Sênior', 'fernanda', 'fer123', 'gerente'),
(7, 'Ricardo Almeida', '666.666.666-66', 'ricardo.almeida@empresa.com', 'Product Owner', 'ricardo', 'rick123', 'gerente'),
(8, 'Lucas Martins', '777.777.777-77', 'lucas.martins@empresa.com', 'DBA', 'lucas', 'lucas123', 'colaborador'),
(9, 'Juliana Pereira', '888.888.888-88', 'juliana.pereira@empresa.com', 'Desenvolvedora Mobile', 'juliana', 'ju123', 'colaborador'),
(10, 'Gabriel Rocha', '999.999.999-99', 'gabriel.rocha@empresa.com', 'Analista de QA', 'gabriel', 'gabi123', 'colaborador'),
(11, 'Mariana Barbosa', '101.010.101-01', 'mariana.barbosa@empresa.com', 'Designer UI/UX', 'mariana', 'mari123', 'colaborador'),
(12, 'Thiago Santos', '121.212.121-21', 'thiago.santos@empresa.com', 'Engenheiro DevOps', 'thiago', 'thiago123', 'colaborador')
ON DUPLICATE KEY UPDATE nome_completo=VALUES(nome_completo), cpf=VALUES(cpf), email=VALUES(email), cargo=VALUES(cargo), login=VALUES(login), senha=VALUES(senha), perfil=VALUES(perfil);

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
(1, 3), (1, 4), (1, 11), -- Equipe Alfa com Colaborador, Beatriz, Mariana
(2, 5), (2, 8),          -- Equipe Beta com Davi, Lucas
(3, 9),                 -- Equipe Gama com Juliana
(4, 10),                -- Equipe Delta com Gabriel
(5, 12)                 -- Equipe Ômega com Thiago
ON DUPLICATE KEY UPDATE id_equipe=VALUES(id_equipe);

-- Projetos (5 exemplos)
INSERT INTO projetos (id, nome_projeto, descricao, data_inicio, data_fim_prevista, status, id_gerente) VALUES
(1, 'Sistema de E-commerce B2C', 'Plataforma completa de vendas online para o consumidor final.', '2024-01-10', '2024-09-30', 'em andamento', 2),
(2, 'Aplicativo de Gestão Financeira', 'App mobile para controle de despesas e receitas pessoais.', '2024-02-20', '2024-11-15', 'em andamento', 6),
(3, 'Modernização do Legado (ERP)', 'Refatoração e migração do sistema ERP interno para novas tecnologias.', '2024-03-01', '2025-06-30', 'planejado', 1),
(4, 'Plataforma de Análise de Dados', 'Ferramenta de BI para visualização de métricas de vendas.', '2024-04-15', '2024-12-20', 'planejado', 7),
(5, 'Website Institucional com CMS', 'Desenvolvimento do novo site da empresa com um sistema de gerenciamento de conteúdo.', '2024-05-05', '2024-08-25', 'concluído', 6)
ON DUPLICATE KEY UPDATE nome_projeto=VALUES(nome_projeto), descricao=VALUES(descricao), status=VALUES(status), id_gerente=VALUES(id_gerente);

-- Associações de Equipes aos Projetos
INSERT INTO projeto_equipes (id_projeto, id_equipe) VALUES
(1, 1), (1, 2), -- E-commerce com Frontend e Backend
(2, 3),         -- App Financeiro com Mobile
(3, 2), (3, 5), -- ERP com Backend e DevOps
(4, 2),         -- BI com Backend
(5, 1)          -- Website com Frontend
ON DUPLICATE KEY UPDATE id_projeto=VALUES(id_projeto);

-- Tarefas (com novos campos de data)
INSERT INTO tarefas (id, titulo, descricao, id_projeto, id_responsavel, status, data_inicio_prevista, data_fim_prevista, data_inicio_real, data_fim_real) VALUES
(1, 'Desenvolver Carrinho de Compras', 'Implementar a funcionalidade completa do carrinho de compras no e-commerce.', 1, 3, 'em execução', '2024-05-08', '2024-06-10', '2024-05-10', NULL),
(2, 'Criar Tela de Login do App', 'Desenvolver a interface e a lógica de autenticação para o aplicativo financeiro.', 2, 9, 'pendente', '2024-05-12', '2024-06-05', NULL, NULL),
(3, 'Configurar Pipeline de CI/CD', 'Automatizar o processo de build e deploy para o projeto de modernização do ERP.', 3, 12, 'em execução', '2024-04-20', '2024-05-30', '2024-04-25', NULL),
(4, 'Testar API de Pagamentos', 'Criar e executar testes de integração para a API de pagamentos do e-commerce.', 1, 10, 'pendente', '2024-05-18', '2024-06-20', NULL, NULL),
(5, 'Migrar Banco de Dados de Clientes', 'Criar script para migrar os dados de clientes do sistema legado para o novo ERP.', 3, 5, 'concluída', '2024-03-28', '2024-04-30', '2024-04-01', '2024-04-28'),
(6, 'Modelar Dashboards de Vendas', 'Definir os principais KPIs e desenhar os dashboards para a plataforma de BI.', 4, 7, 'pendente', '2024-05-22', '2024-06-25', NULL, NULL),
(7, 'Criar Componente de Gráfico', 'Desenvolver um componente reutilizável de gráfico para o portal.', 1, 4, 'em execução', '2024-05-30', '2024-06-30', '2024-06-01', NULL),
(8, 'Implementar Notificações Push', 'Adicionar funcionalidade de notificações push no app financeiro.', 2, 9, 'pendente', '2024-06-08', '2024-07-10', NULL, NULL),
(9, 'Otimizar Consultas do Relatório', 'Analisar e otimizar o desempenho das consultas SQL na plataforma de BI.', 4, 8, 'pendente', '2024-06-12', '2024-07-15', NULL, NULL),
(10, 'Ajustar Layout para Blog', 'Adaptar o layout do novo website para incluir uma seção de blog.', 5, 11, 'concluída', '2024-05-28', '2024-06-20', '2024-06-01', '2024-06-18')
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo), descricao=VALUES(descricao), status=VALUES(status), id_responsavel=VALUES(id_responsavel), data_inicio_prevista=VALUES(data_inicio_prevista), data_fim_prevista=VALUES(data_fim_prevista), data_inicio_real=VALUES(data_inicio_real), data_fim_real=VALUES(data_fim_real);
