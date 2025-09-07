package br.com.projeto.view;

import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela para visualização e gerenciamento de usuários (acessível apenas por administradores).
 */
public class TelaGerenciarUsuarios extends JFrame {

    private JTable tabelaUsuarios;
    private DefaultTableModel tableModel;
    private final UsuarioDAO usuarioDAO;

    public TelaGerenciarUsuarios() {
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Gerenciamento de Usuários");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo = new JButton("Novo Usuário");
        JButton btnEditar = new JButton("Editar Usuário");
        JButton btnExcluir = new JButton("Excluir Usuário");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // --- Tabela de Usuários ---
        String[] colunas = {"ID", "Nome Completo", "Username", "Perfil"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaUsuarios = new JTable(tableModel);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // TODO: Adicionar ações para os botões de novo e editar.

        // Ação para o botão de excluir
        btnExcluir.addActionListener(e -> excluirUsuario());

        // Carrega os dados iniciais
        carregarUsuarios();
    }

    /**
     * Carrega a lista de usuários do banco de dados e atualiza a tabela.
     */
    private void carregarUsuarios() {
        // Limpa a tabela atual
        tableModel.setRowCount(0);

        List<Usuario> usuarios = usuarioDAO.buscarTodos();
        
        for (Usuario usuario : usuarios) {
            Object[] rowData = { 
                usuario.getId(), 
                usuario.getNomeCompleto(), 
                usuario.getUsername(),
                usuario.getPerfil()
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Exclui o usuário selecionado na tabela.
     */
    private void excluirUsuario() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow >= 0) {
            int idUsuario = (int) tabelaUsuarios.getValueAt(selectedRow, 0);
            String nomeUsuario = (String) tabelaUsuarios.getValueAt(selectedRow, 1);

            // Impede a exclusão do admin principal (uma segunda camada de segurança)
            if (idUsuario == 1) {
                JOptionPane.showMessageDialog(this, "O administrador principal não pode ser excluído.", "Ação Proibida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o usuário '" + nomeUsuario + "'?", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                usuarioDAO.excluir(idUsuario);
                carregarUsuarios(); // Atualiza a tabela
                JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um usuário para excluir.", "Nenhum Usuário Selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }
}
