package br.com.projeto.view;

import br.com.projeto.model.Usuario;

import javax.swing.*;

public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;

    // Componentes que terão a visibilidade controlada
    private final JMenuItem itemUsuarios;
    private final JMenu menuRelatorios;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;

        setTitle("Sistema de Gestão de Projetos - Bem-vindo, " + usuarioLogado.getNomeCompleto());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cria a barra de menus
        JMenuBar menuBar = new JMenuBar();

        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);

        // Menu Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemProjetos = new JMenuItem("Projetos");
        JMenuItem itemTarefas = new JMenuItem("Tarefas");
        JMenuItem itemEquipes = new JMenuItem("Equipes");
        itemUsuarios = new JMenuItem("Usuários"); // Atribuído à variável de instância
        menuCadastros.add(itemProjetos);
        menuCadastros.add(itemTarefas);
        menuCadastros.add(itemEquipes);
        menuCadastros.addSeparator();
        menuCadastros.add(itemUsuarios);

        // Menu Relatórios
        menuRelatorios = new JMenu("Relatórios"); // Atribuído à variável de instância
        JMenuItem itemRelatorioProjetos = new JMenuItem("Andamento dos Projetos");
        JMenuItem itemRelatorioDesempenho = new JMenuItem("Desempenho dos Colaboradores");
        menuRelatorios.add(itemRelatorioProjetos);
        menuRelatorios.add(itemRelatorioDesempenho);

        // Adiciona os menus à barra
        menuBar.add(menuArquivo);
        menuBar.add(menuCadastros);
        menuBar.add(menuRelatorios);

        // Define a barra de menus da janela
        setJMenuBar(menuBar);

        // Adiciona um label de boas-vindas
        JLabel welcomeLabel = new JLabel("Bem-vindo ao sistema, " + usuarioLogado.getNomeCompleto() + "! Seu perfil é: " + usuarioLogado.getPerfil(), SwingConstants.CENTER);
        add(welcomeLabel);

        // Aplica as regras de permissão à interface
        configurarPermissoes();
    }

    /**
     * Configura a visibilidade dos menus com base no perfil do usuário logado.
     */
    private void configurarPermissoes() {
        String perfil = usuarioLogado.getPerfil();

        // Converte para minúsculas para evitar problemas com case-sensitive (ex: "Administrador" vs "administrador")
        if (perfil == null) {
            perfil = ""; // Evita NullPointerException
        }

        boolean isAdministrador = "administrador".equalsIgnoreCase(perfil);
        boolean isGerente = "gerente".equalsIgnoreCase(perfil);

        // --- Regras de Visibilidade ---

        // O item de menu "Usuários" só deve ser visível para administradores.
        itemUsuarios.setVisible(isAdministrador);

        // O menu "Relatórios" é visível para administradores e gerentes.
        menuRelatorios.setVisible(isAdministrador || isGerente);
    }
}
