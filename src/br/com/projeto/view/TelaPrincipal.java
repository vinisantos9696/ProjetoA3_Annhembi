package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.DesempenhoColaboradorDTO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;
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
        setLayout(new BorderLayout());

        // --- Barra de Menus ---
        JMenuBar menuBar = new JMenuBar();

        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSair = new JMenuItem("Sair");
        menuArquivo.add(itemSair);

        // Menu Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemProjetos = new JMenuItem("Projetos");
        JMenuItem itemTarefas = new JMenuItem("Tarefas");
        JMenuItem itemEquipes = new JMenuItem("Equipes");
        itemUsuarios = new JMenuItem("Usuários");
        menuCadastros.add(itemProjetos);
        menuCadastros.add(itemTarefas);
        menuCadastros.add(itemEquipes);
        menuCadastros.addSeparator();
        menuCadastros.add(itemUsuarios);

        // Menu Relatórios
        menuRelatorios = new JMenu("Relatórios");
        JMenuItem itemRelatorioProjetos = new JMenuItem("Andamento dos Projetos");
        JMenuItem itemRelatorioDesempenho = new JMenuItem("Desempenho dos Colaboradores");
        menuRelatorios.add(itemRelatorioProjetos);
        menuRelatorios.add(itemRelatorioDesempenho);

        // Menu Sobre
        JMenu menuSobre = new JMenu("Sobre");
        JMenuItem itemInformacoes = new JMenuItem("Informações do Sistema");
        menuSobre.add(itemInformacoes);

        menuBar.add(menuArquivo);
        menuBar.add(menuCadastros);
        menuBar.add(menuRelatorios);
        menuBar.add(menuSobre);
        setJMenuBar(menuBar);

        // --- Painel de Boas-Vindas ---
        JLabel welcomeLabel = new JLabel("Bem-vindo ao sistema, " + usuarioLogado.getNomeCompleto() + "! Seu perfil é: " + usuarioLogado.getPerfil(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(welcomeLabel, BorderLayout.CENTER);

        // --- Ações dos Menus ---
        itemSair.addActionListener(e -> System.exit(0));
        itemInformacoes.addActionListener(e -> abrirTelaSobre());
        itemProjetos.addActionListener(e -> abrirGerenciamentoProjetos());
        itemEquipes.addActionListener(e -> abrirGerenciamentoEquipes());
        itemTarefas.addActionListener(e -> abrirGerenciamentoTarefas());
        itemUsuarios.addActionListener(e -> abrirGerenciamentoUsuarios());
        itemRelatorioProjetos.addActionListener(e -> abrirRelatorioProjetos());
        itemRelatorioDesempenho.addActionListener(e -> abrirRelatorioDesempenho());

        // Aplica as regras de permissão à interface
        configurarPermissoes();
    }

    private void configurarPermissoes() {
        String perfil = usuarioLogado.getPerfil();
        boolean isAdministrador = "administrador".equalsIgnoreCase(perfil);
        boolean isGerente = "gerente".equalsIgnoreCase(perfil);

        itemUsuarios.setVisible(isAdministrador);
        menuRelatorios.setVisible(isAdministrador || isGerente);
    }

    private void abrirTelaSobre() {
        TelaSobreDialog sobreDialog = new TelaSobreDialog(this);
        sobreDialog.setVisible(true);
    }

    private void abrirGerenciamentoProjetos() {
        TelaGerenciarProjetos telaGerenciar = new TelaGerenciarProjetos(usuarioLogado);
        telaGerenciar.setVisible(true);
    }

    private void abrirGerenciamentoEquipes() {
        TelaGerenciarEquipes telaGerenciar = new TelaGerenciarEquipes(usuarioLogado);
        telaGerenciar.setVisible(true);
    }

    private void abrirGerenciamentoTarefas() {
        TelaGerenciarTarefas telaGerenciar = new TelaGerenciarTarefas(usuarioLogado);
        telaGerenciar.setVisible(true);
    }

    private void abrirGerenciamentoUsuarios() {
        TelaGerenciarUsuarios telaGerenciar = new TelaGerenciarUsuarios();
        telaGerenciar.setVisible(true);
    }

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
