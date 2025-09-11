# Sistema de Gestão de Projetos e Equipes

## 1. Visão Geral

Este é um sistema de desktop completo para a gestão de projetos, tarefas e equipes, desenvolvido em Java com uma interface gráfica Swing e um banco de dados MySQL. A aplicação permite o controle de acesso baseado em perfis de usuário, garantindo que cada membro da equipe tenha acesso apenas às funcionalidades pertinentes à sua função.

O sistema foi projetado para ser uma solução robusta e intuitiva para pequenas e médias equipes que precisam organizar seus fluxos de trabalho, monitorar o progresso e avaliar o desempenho.

## 2. Funcionalidades Principais

*   **Autenticação Segura:** Tela de login que valida as credenciais do usuário contra o banco de dados.
*   **Controle de Acesso por Perfil:**
    *   **Administrador:** Acesso total ao sistema, incluindo gerenciamento de usuários.
    *   **Gerente:** Pode gerenciar projetos, equipes e tarefas, além de visualizar relatórios.
    *   **Colaborador:** Pode visualizar projetos, equipes e tarefas, mas não pode editar ou criar novos registros.
*   **Gerenciamento Completo (CRUD):**
    *   **Projetos:** Crie, visualize, edite e exclua projetos, atribuindo um gerente responsável.
    *   **Equipes:** Crie, visualize, edite e exclua equipes.
    *   **Tarefas:** Crie, visualize, edite e exclua tarefas, vinculando-as a projetos e a um usuário responsável.
    *   **Usuários:** O administrador pode criar, visualizar, editar e excluir outros usuários.
*   **Relatórios Detalhados:**
    *   **Andamento dos Projetos:** Gera um relatório com o status, datas e gerente de todos os projetos.
    *   **Desempenho dos Colaboradores:** Gera um relatório que resume a quantidade de tarefas (total, concluídas, em andamento) por usuário.
*   **Interface Gráfica Amigável:** Interface construída com Java Swing, proporcionando uma experiência de usuário clara e funcional.

## 3. Tecnologias Utilizadas

*   **Linguagem:** Java
*   **Interface Gráfica:** Java Swing
*   **Banco de Dados:** MySQL
*   **Build Tool:** Maven (implícito pelo uso de dependências como o MySQL Connector)
*   **IDE de Desenvolvimento:** IntelliJ IDEA (ou outra de sua preferência)

## 4. Configuração e Execução

Siga estes passos para configurar e executar o projeto em sua máquina local.

### Pré-requisitos

*   **Java Development Kit (JDK):** Versão 8 ou superior.
*   **MySQL Server:** Versão 8 ou superior.
*   **IDE Java:** IntelliJ IDEA, Eclipse ou outra de sua preferência.

### Passo 1: Configurar as Variáveis de Ambiente

A aplicação precisa saber o **usuário** e a **senha** do seu banco de dados MySQL. Isso é feito de forma segura através de variáveis de ambiente na sua IDE.

**No IntelliJ IDEA:**

1.  Abra o projeto.
2.  No canto superior direito, clique no menu de configurações de execução e selecione **`Edit Configurations...`**.
3.  Selecione a configuração de execução da sua classe `Main`.
4.  Encontre o campo **`Environment variables`** e clique no ícone de pasta ou no botão `...` para abrir o editor.
5.  Cole o texto abaixo, **substituindo `seu_usuario_mysql` e `sua_senha_aqui` pelos seus dados reais**.

    ```
    DB_USER=seu_usuario_mysql;DB_PASSWORD=sua_senha_aqui
    ```
    *Exemplo prático: `DB_USER=root;DB_PASSWORD=12345`*

6.  Clique em **OK** para salvar as variáveis e depois em **Apply** e **OK** para fechar as configurações.

### Passo 2: Executar o Projeto

1.  Abra a classe `br.com.projeto.main.Main.java`.
2.  Clique no botão verde de "Play" ao lado do método `main` para executar a aplicação.

> **Como funciona o Banco de Dados:** Na primeira vez que a aplicação é executada, ela cria o banco de dados `gestao_projetos_db` e o popula com dados de exemplo. A partir da segunda execução, a aplicação apenas se conecta ao banco de dados existente, **preservando todos os dados** que foram cadastrados.

## 5. Guia de Testes do Sistema

Para testar todas as funcionalidades, faça o login com os três perfis de usuário padrão. Após a primeira execução, os dados que você criar serão mantidos.

**Credenciais Padrão (criadas na primeira execução):**
*   **Administrador:**
    *   **Usuário:** `admin`
    *   **Senha:** `admin`
*   **Gerente:**
    *   **Usuário:** `gerente`
    *   **Senha:** `gerente`
*   **Colaborador:**
    *   **Usuário:** `colab`
    *   **Senha:** `colab`

---

### Cenário 1: Teste como Administrador

1.  **Login:** Use as credenciais de `admin`.
2.  **Verificar Acesso:** Confirme que os menus **"Cadastros"** (com "Usuários") e **"Relatórios"** estão visíveis.
3.  **Gerenciar Usuários:**
    *   Vá em `Cadastros -> Usuários`.
    *   Clique em **"Novo Usuário"**, crie um usuário de teste e salve. Verifique se ele aparece na lista.
    *   Selecione o usuário criado, clique em **"Editar Usuário"**, altere seu nome e salve. Verifique a alteração.
    *   Selecione o usuário criado e clique em **"Excluir Usuário"**. Confirme a exclusão.

---

### Cenário 2: Teste como Gerente

1.  **Login:** Use as credenciais de `gerente`.
2.  **Verificar Acesso:**
    *   Confirme que o menu **"Relatórios"** está visível.
    *   Confirme que no menu **"Cadastros"**, o item **"Usuários"** **NÃO** está visível.
3.  **Gerenciar Projetos, Equipes e Tarefas:**
    *   Vá em `Cadastros -> Projetos` (e depois em Equipes e Tarefas).
    *   Verifique se os botões **"Novo"**, **"Editar"** e **"Excluir"** estão **habilitados**. Teste-os.

---

### Cenário 3: Teste como Colaborador

1.  **Login:** Use as credenciais de `colab`.
2.  **Verificar Acesso:**
    *   Confirme que os menus **"Relatórios"** e **"Usuários"** **NÃO** estão visíveis.
3.  **Verificar Permissões de Edição:**
    *   Vá em `Cadastros -> Projetos` (e depois em Equipes e Tarefas).
    *   Verifique se os botões **"Novo"**, **"Editar"** e **"Excluir"** estão **desabilitados**.
