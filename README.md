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

## 4. Configuração do Ambiente (Passo a Passo)

Siga estes passos para configurar e executar o projeto em sua máquina local.

### Passo 1: Pré-requisitos

*   **Java Development Kit (JDK):** Versão 8 ou superior.
*   **MySQL Server:** Versão 8 ou superior.
*   **IDE Java:** IntelliJ IDEA, Eclipse ou outra de sua preferência.
*   **Cliente MySQL:** MySQL Workbench, DBeaver ou outro para gerenciar o banco de dados.

### Passo 2: Configurar o Banco de Dados

1.  Abra seu cliente MySQL e conecte-se ao seu servidor de banco de dados.
2.  Crie um novo banco de dados (schema) com o nome `gestao_projetos_db`.
    ```sql
    CREATE DATABASE gestao_projetos_db;
    ```
3.  **Não é necessário executar o script `schema.sql` manualmente.** A aplicação Java foi projetada para criar e popular a estrutura do banco de dados automaticamente na primeira execução.

### Passo 3: Configurar as Variáveis de Ambiente na IDE

Para que a aplicação possa se conectar ao banco de dados, você precisa informar o usuário e a senha. Isso é feito de forma segura através de variáveis de ambiente.

**No IntelliJ IDEA:**

1.  Abra o projeto.
2.  No canto superior direito, clique no menu de configurações de execução e selecione **`Edit Configurations...`**.
3.  Selecione a configuração de execução da sua classe `Main`.
4.  Encontre o campo **`Environment variables`** e clique no ícone de pasta ou no botão `...` para abrir o editor.
5.  Adicione duas novas variáveis:
    *   **Name:** `DB_USER`
        *   **Value:** `root` (ou o seu usuário do MySQL)
    *   **Name:** `DB_PASSWORD`
        *   **Value:** `sua_senha_aqui` (substitua pela sua senha real do MySQL)
6.  Clique em **OK** para salvar as variáveis e depois em **Apply** e **OK** para fechar as configurações.

### Passo 4: Executar o Projeto

1.  Abra a classe `br.com.projeto.main.Main.java`.
2.  Clique no botão verde de "Play" ao lado do método `main` para executar a aplicação.
3.  Na primeira vez, o sistema irá configurar o banco de dados e, em seguida, a tela de login aparecerá.

## 5. Guia de Testes do Sistema

Para testar todas as funcionalidades, faça o login com os três perfis de usuário padrão.

**Credenciais Padrão:**
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
4.  **Gerenciar Projetos:**
    *   Vá em `Cadastros -> Projetos`.
    *   Teste os botões **"Novo"**, **"Editar"** e **"Excluir"** como fez para usuários.
5.  **Gerar Relatórios:**
    *   Vá em `Relatórios -> Andamento dos Projetos`. Verifique se a janela abre com os dados.
    *   Vá em `Relatórios -> Desempenho dos Colaboradores`. Verifique se a janela abre com os dados.

---

### Cenário 2: Teste como Gerente

1.  **Login:** Use as credenciais de `gerente`.
2.  **Verificar Acesso:**
    *   Confirme que o menu **"Relatórios"** está visível.
    *   Confirme que no menu **"Cadastros"**, o item **"Usuários"** **NÃO** está visível.
3.  **Gerenciar Projetos:**
    *   Vá em `Cadastros -> Projetos`.
    *   Verifique se os botões **"Novo"**, **"Editar"** e **"Excluir"** estão **habilitados**. Teste-os.
4.  **Gerenciar Equipes e Tarefas:**
    *   Repita o teste acima para as telas de `Equipes` e `Tarefas`.

---

### Cenário 3: Teste como Colaborador

1.  **Login:** Use as credenciais de `colab`.
2.  **Verificar Acesso:**
    *   Confirme que o menu **"Relatórios"** **NÃO** está visível.
    *   Confirme que no menu **"Cadastros"**, o item **"Usuários"** **NÃO** está visível.
3.  **Verificar Permissões de Edição:**
    *   Vá em `Cadastros -> Projetos`.
    *   Verifique se os botões **"Novo"**, **"Editar"** e **"Excluir"** estão **desabilitados**.
    *   Repita a verificação para as telas de `Equipes` e `Tarefas`.
