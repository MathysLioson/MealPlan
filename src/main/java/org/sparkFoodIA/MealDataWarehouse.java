package org.sparkFoodIA;


import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MealDataWarehouse {

    public void saveMealsInCSV(List<Meal> meals, String mealType, String dietChoice) {
        String csvFile = "src/main/resources/datawarehouse/mealPlan_" + mealType + "_" + dietChoice + ".csv";
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Day,Meal Name,Energy (kcal),Fat (g),Saturated Fat (g),Carbohydrates (g),Sugars (g),Salt (g),Vitamin A (µg),Vitamin C (mg),Calcium (mg),Iron (mg),Image URL\n");
            for (Meal meal : meals) {
                writer.append(meal.getDayIndex() + "," + meal.getMealName() + "," + meal.getEnergyKcal() + "," + meal.getFat() + "," + meal.getSaturatedFat() + "," + meal.getCarbohydrates() + "," + meal.getSugars() + "," + meal.getSalt() + "," + meal.getVitaminA() + "," + meal.getVitaminC() + "," + meal.getCalcium() + "," + meal.getIron() + "," + meal.getImageUrl() + "\n");
            }
            System.out.println("Le fichier " + csvFile + " a été créé avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier CSV : " + e.getMessage());
        }
    }
}
