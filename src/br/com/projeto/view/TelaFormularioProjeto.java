package br.com.projeto.view;

import br.com.projeto.dao.ProjetoDAO;
import br.com.projeto.dao.UsuarioDAO;
import br.com.projeto.model.Projeto;
import br.com.projeto.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Tela de formulário para criar ou editar um Projeto.
 */
public class TelaFormularioProjeto extends JDialog {

    private final JTextField txtNome = new JTextField(40);
    private final JTextArea txtDescricao = new JTextArea(5, 40);
    private final JTextField txtDataInicio = new JTextField(10);
    private final JTextField txtDataFim = new JTextField(10);
    private final JComboBox<String> cmbStatus;
    private final JComboBox<Usuario> cmbGerente;

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final ProjetoDAO projetoDAO;
    private Projeto projeto; // Nulo se for um novo projeto, preenchido se for edição
    private boolean salvo = false; // Flag para indicar se o formulário foi salvo com sucesso

    public TelaFormularioProjeto(Frame owner, Projeto projeto) {
        super(owner, true); // Diálogo modal
        this.projeto = projeto;
        this.projetoDAO = new ProjetoDAO();

        setTitle(projeto == null ? "Novo Projeto" : "Editar Projeto");
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Configura a quebra de linha automática para a área de descrição
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        // --- Painel do Formulário ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels e Campos
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(txtNome, gbc);
        gbc.weightx = 0.0; // Reseta o peso

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH; // Preenche na horizontal e vertical
        gbc.weightx = 1.0; // Permite que a coluna cresça na horizontal
        gbc.weighty = 1.0; // Permite que a linha cresça na vertical
        formPanel.add(new JScrollPane(txtDescricao), gbc);
        // Reseta os pesos para os próximos componentes
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Data de Início (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtDataInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Data de Fim (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtDataFim, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Planejado", "Em Andamento", "Concluído", "Cancelado"});
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Gerente:"), gbc);
        cmbGerente = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbGerente, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações e Preenchimento ---
        carregarGerentes();
        preencherFormulario();

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarProjeto());
    }

    /**
     * Busca os usuários com perfil de gerente ou administrador e os adiciona ao ComboBox.
     */
    private void carregarGerentes() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> gerentes = usuarioDAO.buscarGerentes(); 
        for (Usuario gerente : gerentes) {
            cmbGerente.addItem(gerente);
        }
    }

    /**
     * Se for uma edição, preenche os campos com os dados do projeto existente.
     */
    private void preencherFormulario() {
        if (projeto != null) {
            txtNome.setText(projeto.getNome());
            txtDescricao.setText(projeto.getDescricao());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (projeto.getDataInicio() != null) {
                txtDataInicio.setText(sdf.format(projeto.getDataInicio()));
            }
            if (projeto.getDataFim() != null) {
                txtDataFim.setText(sdf.format(projeto.getDataFim()));
            }

            cmbStatus.setSelectedItem(projeto.getStatus());

            // Seleciona o gerente correto no ComboBox
            if (projeto.getGerente() != null) {
                for (int i = 0; i < cmbGerente.getItemCount(); i++) {
                    if (cmbGerente.getItemAt(i).getId() == projeto.getGerente().getId()) {
                        cmbGerente.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Valida os dados, cria ou atualiza o objeto Projeto e o salva no banco.
     */
    private void salvarProjeto() {
        // Validação simples
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do projeto é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria um novo objeto se for um novo projeto
        if (this.projeto == null) {
            this.projeto = new Projeto();
        }

        // Preenche o objeto com os dados do formulário
        projeto.setNome(txtNome.getText().trim());
        projeto.setDescricao(txtDescricao.getText().trim());
        projeto.setStatus((String) cmbStatus.getSelectedItem());
        projeto.setGerente((Usuario) cmbGerente.getSelectedItem());

        // Converte as datas
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (!txtDataInicio.getText().trim().isEmpty()) {
                projeto.setDataInicio(sdf.parse(txtDataInicio.getText().trim()));
            }
            if (!txtDataFim.getText().trim().isEmpty()) {
                projeto.setDataFim(sdf.parse(txtDataFim.getText().trim()));
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/mm/aaaa.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Salva no banco de dados
        projetoDAO.salvar(projeto);

        this.salvo = true; // Indica que foi salvo com sucesso
        dispose(); // Fecha o diálogo
    }

    /**
     * Método para a tela pai saber se o diálogo foi fechado após salvar.
     * @return true se o projeto foi salvo, false caso contrário.
     */
    public boolean isSalvo() {
        return salvo;
    }
}
