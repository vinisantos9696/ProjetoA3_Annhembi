package br.com.projeto.view;

import br.com.projeto.dao.EquipeDAO;
import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.TarefaDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.DesempenhoColaboradorDTO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.util.List;

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

        // --- AÇÕES DOS MENUS DE CADASTRO ---
        itemProjetos.addActionListener(e -> abrirGerenciamentoProjetos());
        itemEquipes.addActionListener(e -> abrirGerenciamentoEquipes());
        itemTarefas.addActionListener(e -> abrirGerenciamentoTarefas());
        itemUsuarios.addActionListener(e -> abrirGerenciamentoUsuarios());

        // Menu Relatórios
        menuRelatorios = new JMenu("Relatórios"); // Atribuído à variável de instância
        JMenuItem itemRelatorioProjetos = new JMenuItem("Andamento dos Projetos");
        JMenuItem itemRelatorioDesempenho = new JMenuItem("Desempenho dos Colaboradores");
        menuRelatorios.add(itemRelatorioProjetos);
        menuRelatorios.add(itemRelatorioDesempenho);

        // --- AÇÕES DOS MENUS DE RELATÓRIO ---
        itemRelatorioProjetos.addActionListener(e -> abrirRelatorioProjetos());
        itemRelatorioDesempenho.addActionListener(e -> abrirRelatorioDesempenho());

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

        if (perfil == null) {
            perfil = ""; // Evita NullPointerException
        }

        boolean isAdministrador = "administrador".equalsIgnoreCase(perfil);
        boolean isGerente = "gerente".equalsIgnoreCase(perfil);

        // O item de menu "Usuários" só deve ser visível para administradores.
        itemUsuarios.setVisible(isAdministrador);

        // O menu "Relatórios" é visível para administradores e gerentes.
        menuRelatorios.setVisible(isAdministrador || isGerente);
    }

    /**
     * Abre a tela de gerenciamento de projetos.
     */
    private void abrirGerenciamentoProjetos() {
        TelaGerenciarProjetos telaGerenciar = new TelaGerenciarProjetos(usuarioLogado);
        telaGerenciar.setVisible(true);
    }

    /**
     * Abre a tela de gerenciamento de equipes.
     */
    private void abrirGerenciamentoEquipes() {
        TelaGerenciarEquipes telaGerenciar = new TelaGerenciarEquipes(usuarioLogado);
        telaGerenciar.setVisible(true);
    }

    /**
     * Abre a tela de gerenciamento de tarefas.
     */
    private void abrirGerenciamentoTarefas() {
        TelaGerenciarTarefas telaGerenciar = new TelaGerenciarTarefas(usuarioLogado);
        telaGerenciar.setVisible(true);
    }

    /**
     * Abre a tela de gerenciamento de usuários.
     */
    private void abrirGerenciamentoUsuarios() {
        TelaGerenciarUsuarios telaGerenciar = new TelaGerenciarUsuarios();
        telaGerenciar.setVisible(true);
    }

    /**
     * Busca os dados dos projetos e abre a tela de relatório correspondente.
     */
    private void abrirRelatorioProjetos() {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        List<Projeto> projetos = projetoDAO.buscarTodosParaRelatorio();

        if (projetos != null && !projetos.isEmpty()) {
            TelaRelatorioProjetos telaRelatorio = new TelaRelatorioProjetos(projetos);
            telaRelatorio.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Não há projetos para exibir no relatório.", "Relatório Vazio", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Busca os dados de desempenho dos colaboradores e abre a tela de relatório.
     */
    private void abrirRelatorioDesempenho() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<DesempenhoColaboradorDTO> relatorio = usuarioDAO.gerarRelatorioDesempenho();

        if (relatorio != null && !relatorio.isEmpty()) {
            TelaRelatorioDesempenho telaRelatorio = new TelaRelatorioDesempenho(relatorio);
            telaRelatorio.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Não há dados de desempenho para exibir.", "Relatório Vazio", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
