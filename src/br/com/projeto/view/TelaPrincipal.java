package br.com.projeto.view;

import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {

    private Usuario usuarioLogado;

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
        JMenuItem itemUsuarios = new JMenuItem("Usuários");
        menuCadastros.add(itemProjetos);
        menuCadastros.add(itemTarefas);
        menuCadastros.add(itemEquipes);
        menuCadastros.addSeparator();
        menuCadastros.add(itemUsuarios);

        // Menu Relatórios
        JMenu menuRelatorios = new JMenu("Relatórios");
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
        
        // TODO: Adicionar lógica de permissões para habilitar/desabilitar menus com base no perfil do usuário.
    }
}
