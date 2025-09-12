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
    private List<Usuario> usuariosAtuais;

    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    public TelaGerenciarUsuarios() {
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Gerenciamento de Usuários");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo Usuário");
        btnEditar = new JButton("Editar Usuário");
        btnExcluir = new JButton("Excluir Usuário");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // --- Tabela de Usuários ---
        String[] colunas = {"ID", "Nome Completo", "Login", "Perfil"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaUsuarios = new JTable(tableModel);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);

        // Adiciona os componentes à janela
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões ---
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getUsuarioSelecionado()));
        btnExcluir.addActionListener(e -> excluirUsuario());

        // Carrega os dados iniciais
        carregarUsuarios();
    }

    /**
     * Carrega a lista de usuários do banco de dados e atualiza a tabela.
     */
    private void carregarUsuarios() {
        tableModel.setRowCount(0);
        this.usuariosAtuais = usuarioDAO.buscarTodos();
        
        for (Usuario usuario : this.usuariosAtuais) {
            Object[] rowData = { 
                usuario.getId(), 
                usuario.getNomeCompleto(), 
                usuario.getLogin(),
                usuario.getPerfil()
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Abre o formulário para criar ou editar um usuário.
     * @param usuario O usuário a ser editado, ou null para um novo.
     */
    private void abrirFormulario(Usuario usuario) {
        if (usuario == null && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um usuário para editar.", "Nenhum Usuário Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Impede a edição do admin principal
        if (usuario != null && usuario.getId() == 1 && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "O administrador principal não pode ser editado.", "Ação Proibida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TelaFormularioUsuario formulario = new TelaFormularioUsuario(this, usuario);
        formulario.setVisible(true);

        if (formulario.isSalvo()) {
            carregarUsuarios();
        }
    }

    /**
     * Pega o objeto Usuario correspondente à linha selecionada na tabela.
     * @return O usuário selecionado, ou null se nenhuma linha for selecionada.
     */
    private Usuario getUsuarioSelecionado() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow >= 0) {
            int usuarioId = (int) tableModel.getValueAt(selectedRow, 0);
            return usuariosAtuais.stream().filter(u -> u.getId() == usuarioId).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Exclui o usuário selecionado na tabela.
     */
    private void excluirUsuario() {
        Usuario usuario = getUsuarioSelecionado();
        if (usuario != null) {
            if (usuario.getId() == 1) {
                JOptionPane.showMessageDialog(this, "O administrador principal não pode ser excluído.", "Ação Proibida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o usuário '" + usuario.getNomeCompleto() + "'?", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                usuarioDAO.excluir(usuario.getId());
                carregarUsuarios();
                JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um usuário para excluir.", "Nenhum Usuário Selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }
}
