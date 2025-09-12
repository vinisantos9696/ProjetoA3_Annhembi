package br.com.projeto.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Janela de diálogo que exibe as informações sobre o sistema e seus criadores.
 */
public class TelaSobreDialog extends JDialog {

    public TelaSobreDialog(Frame owner) {
        super(owner, "Sobre o Sistema", true); // Título da janela e modal
        setSize(500, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Painel Principal com Margem ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Título e Versão ---
        JLabel titleLabel = new JLabel("Sistema de Gestão de Projetos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel versionLabel = new JLabel("Versão: 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(versionLabel, BorderLayout.CENTER);

        // --- Painel dos Criadores ---
        JPanel creatorsPanel = new JPanel(new BorderLayout());
        creatorsPanel.setBorder(BorderFactory.createTitledBorder("Desenvolvido por"));

        String[] creators = {
            "BRUNA CAMILA LAZARINI",
            "LUCIANA XAVIER DOS SANTOS",
            "MARCO AURÉLIO TEIXEIRA",
            "MAURÍCIO CIRQUEIRA SILVA",
            "CLEBER SOUZA FREITAS",
            "MARCUS VINICIUS SANTOS DE CARVALHO",
            "KALEL POVOA",
            "JÚLIO CESAR ALVES DE ARAUJO",
            "ADRIELI AUGUSTA MENDES",
            "JESSICA MOREIRA DE SOUSA"
        };

        JList<String> creatorsList = new JList<>(creators);
        creatorsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        creatorsList.setBackground(getBackground());
        creatorsList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Impede a seleção
            }
        });

        creatorsPanel.add(new JScrollPane(creatorsList), BorderLayout.CENTER);

        // Adiciona os painéis ao painel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(creatorsPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
}
