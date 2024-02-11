package org.sparkFoodIA;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserChoiceGUI {
    private String DietChoice;
    public UserChoiceGUI() {
        // Création de la fenêtre
        JFrame frame = new JFrame("User Diet Choice");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Création du panel
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // Affichage de la fenêtre
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Création du label
        JLabel userLabel = new JLabel("Choose your diet:");
        userLabel.setBounds(10, 20, 100, 25);
        panel.add(userLabel);

        // Création de la liste déroulante
        String[] diets = {"Vegetarian", "Mediterranean", "Gluten-free", "Ketogenic", "DASH", "Balanced"};
        JComboBox<String> dietList = new JComboBox<>(diets);
        dietList.setBounds(120, 20, 160, 25);
        panel.add(dietList);

        // Création du bouton
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 80, 80, 25);
        panel.add(submitButton);

        // Ajout de l'écouteur d'événements au bouton
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupération du choix de l'utilisateur
                String selectedDiet = (String) dietList.getSelectedItem();
                // Vous pouvez utiliser le choix récupéré ici
                DietChoice = selectedDiet;

                // Fermeture de la fenêtre
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                frame.dispose();
            }
        });
    }

    public String getDIETCHOICE() {
        return DietChoice;
    }
}
