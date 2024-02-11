package org.sparkFoodIA;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MealGUI extends JFrame {

    public MealGUI(List<Meal> breakfastMeals, List<Meal> lunchMeals, List<Meal> dinnerMeals, String DietChoice) {
        setTitle("Weekly Meal Plan");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Monday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 0));
        tabbedPane.addTab("Tuesday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 1));
        tabbedPane.addTab("Wednesday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 2));
        tabbedPane.addTab("Thursday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 3));
        tabbedPane.addTab("Friday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 4));
        tabbedPane.addTab("Saturday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 5));
        tabbedPane.addTab("Sunday", createDailyPanel(breakfastMeals, lunchMeals, dinnerMeals, 6));

        // ajouter l'affichage du nom du regime
        JLabel dietLabel = new JLabel("Diet: " + DietChoice);
        mainPanel.add(dietLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(mainPanel);
    }

    private JPanel createDailyPanel(List<Meal> breakfastMeals, List<Meal> lunchMeals, List<Meal> dinnerMeals, int dayIndex) {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(createMealPanel(breakfastMeals, "Breakfast", dayIndex));
        panel.add(createMealPanel(lunchMeals, "Lunch", dayIndex));
        panel.add(createMealPanel(dinnerMeals, "Dinner", dayIndex));
        return panel;
    }

    private JPanel createMealPanel(List<Meal> meals, String mealType, int dayIndex) {
        JPanel panel = new JPanel(new GridLayout(0, 2)); // 2 colonnes : une pour les boutons, une pour les images
        panel.setBorder(BorderFactory.createTitledBorder(mealType));

        for (Meal meal : meals) {
            if (meal.getDayIndex() == dayIndex) {
                JButton button = new JButton(meal.getMealName());
                button.addActionListener(e -> displayMealDetails(meal));
                panel.add(button);
                if (meal.getImageUrl() != null && meal.getImageUrl().startsWith("http")) {
                    BufferedImage previewImage = null;
                    try {
                        previewImage = ImageIO.read(new URL(meal.getImageUrl()));
                    } catch (IOException e) {
                        System.err.println("Error reading image from URL: " + e.getMessage());
                    }
                    if (previewImage !=null) {
                        JLabel label = new JLabel(new ImageIcon(previewImage));
                        panel.add(label);
                    } else {
                        // Ajouter un espace vide si l'image n'est pas disponible
                        panel.add(new JLabel());
                    }

                } else {
                    // Ajouter un espace vide si aucune image n'est disponible
                    panel.add(new JLabel());
                }
            }
        }
        return panel;
    }


    private void displayMealDetails(Meal meal) {
        // Example implementation: Display meal details in a dialog box
        JOptionPane.showMessageDialog(this,
                "Meal Name: " + meal.getMealName() + "\n" +
                        "Countries: " + meal.getCountries() + "\n" +
                        "Allergens: " + meal.getAllergens() + "\n" +
                        "Traces: " + meal.getTraces() + "\n" +
                        "Serving Size: " + meal.getServingSize() + "\n" +
                        "Energy (KJ): " + meal.getEnergyKj() + "\n" +
                        "Energy (Kcal): " + meal.getEnergyKcal() + "\n" +
                        "Fat: " + meal.getFat() + "\n" +
                        "Saturated Fat: " + meal.getSaturatedFat() + "\n" +
                        "Carbohydrates: " + meal.getCarbohydrates() + "\n" +
                        "Sugars: " + meal.getSugars() + "\n" +
                        "Fiber: " + meal.getFiber() + "\n" +
                        "Proteins: " + meal.getProteins() + "\n" +
                        "Salt: " + meal.getSalt() + "\n" +
                        "Vitamin A: " + meal.getVitaminA() + "\n" +
                        "Vitamin C: " + meal.getVitaminC() + "\n" +
                        "Calcium: " + meal.getCalcium() + "\n" +
                        "Iron: " + meal.getIron() + "\n" +
                        "Product Quantity: " + meal.getProductQuantity() + "\n" +
                        "Ingredients Analysis Tags: " + meal.getIngredientsAnalysisTags(),
                "Meal Details",
                JOptionPane.INFORMATION_MESSAGE);
    }


}
