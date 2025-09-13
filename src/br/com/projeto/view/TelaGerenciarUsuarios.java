package br.com.projeto.view;

import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
    private TableRowSorter<DefaultTableModel> sorter;

    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JTextField txtFiltro;
    private final JComboBox<String> cmbColunaFiltro;

    public TelaGerenciarUsuarios() {
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Gerenciamento de Usuários");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Painel Superior (com botões e filtro) ---
        JPanel painelSuperior = new JPanel(new BorderLayout());

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo Usuário");
        btnEditar = new JButton("Editar Usuário");
        btnExcluir = new JButton("Excluir Usuário");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelSuperior.add(painelBotoes, BorderLayout.NORTH);

        // --- Tabela de Usuários (precisa ser criada antes do painel de filtro) ---
        String[] colunas = {"ID", "Nome Completo", "CPF", "E-mail", "Cargo", "Login", "Perfil"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaUsuarios = new JTable(tableModel);

        // Painel de Filtro
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltro.add(new JLabel("Filtrar por:"));
        cmbColunaFiltro = new JComboBox<>();
        cmbColunaFiltro.addItem("Todas as Colunas");
        for (String coluna : colunas) {
            cmbColunaFiltro.addItem(coluna);
        }
        painelFiltro.add(cmbColunaFiltro);
        painelFiltro.add(new JLabel("Valor:"));
        txtFiltro = new JTextField(30);
        painelFiltro.add(txtFiltro);
        painelSuperior.add(painelFiltro, BorderLayout.SOUTH);

        // Configuração final da tabela
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        tabelaUsuarios.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);

        // Adiciona os componentes à janela
        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Ações dos Botões e Filtro ---
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormulario(getUsuarioSelecionado()));
        btnExcluir.addActionListener(e -> excluirUsuario());

        DocumentListener listenerFiltro = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override
            public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override
            public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        };
        txtFiltro.getDocument().addDocumentListener(listenerFiltro);
        cmbColunaFiltro.addActionListener(e -> aplicarFiltro());

        // Carrega os dados iniciais
        carregarUsuarios();
    }

    private void aplicarFiltro() {
        String texto = txtFiltro.getText();
        String colunaSelecionada = (String) cmbColunaFiltro.getSelectedItem();

        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            int indiceColuna = -1;
            if (colunaSelecionada != null && !"Todas as Colunas".equals(colunaSelecionada)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (tableModel.getColumnName(i).equals(colunaSelecionada)) {
                        indiceColuna = i;
                        break;
                    }
                }
            }

            if (indiceColuna != -1) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, indiceColuna));
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
            }
        }
    }

    private void carregarUsuarios() {
        tableModel.setRowCount(0);
        this.usuariosAtuais = usuarioDAO.buscarTodos();
        for (Usuario usuario : this.usuariosAtuais) {
            tableModel.addRow(new Object[]{
                String.format("%03d", usuario.getId()), // Formata o ID
                usuario.getNomeCompleto(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getCargo(),
                usuario.getLogin(),
                usuario.getPerfil()
            });
        }
    }

    private void abrirFormulario(Usuario usuario) {
        if (usuario == null && btnEditar.isFocusOwner()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um usuário para editar.", "Nenhum Usuário Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TelaFormularioUsuario formulario = new TelaFormularioUsuario(this, usuario);
        formulario.setVisible(true);
        if (formulario.isSalvo()) {
            carregarUsuarios();
        }
    }

    private Usuario getUsuarioSelecionado() {
        int selectedRow = tabelaUsuarios.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tabelaUsuarios.convertRowIndexToModel(selectedRow);
            String idFormatado = (String) tableModel.getValueAt(modelRow, 0);
            int usuarioId = Integer.parseInt(idFormatado); // Converte de volta para int
            return usuariosAtuais.stream().filter(u -> u.getId() == usuarioId).findFirst().orElse(null);
        }
        return null;
    }

    private void excluirUsuario() {
        Usuario usuario = getUsuarioSelecionado();
        if (usuario != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o usuário '" + usuario.getNomeCompleto() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                usuarioDAO.excluir(usuario.getId());
                carregarUsuarios();
                aplicarFiltro();
                JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um usuário para excluir.", "Nenhum Usuário Selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }
}
