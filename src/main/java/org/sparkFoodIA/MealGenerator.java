package org.sparkFoodIA;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MealGenerator {

    // Listes de produits pour le petit-déjeuner, le déjeuner et le dîner
    private List<Meal> breakfastMeals = new ArrayList<>();
    private List<Meal> lunchMeals = new ArrayList<>();
    private List<Meal> dinnerMeals = new ArrayList<>();
    private String DietChoice;
    private Dataset<Row> filteredDataset;
    private Meal todayBreakfast;
    private Meal todayLunch;
    private Meal todayDinner;

    // Limites nutritionnelles journalières
    private double MAX_ENERGY_KCAL;
    private double MAX_FAT;
    private double MAX_SATURATED_FAT;
    private double MAX_CARBOHYDRATES;
    private double MAX_SUGARS;
    private double MAX_SALT;
    private double MAX_VITAMIN_A;
    private double MAX_VITAMIN_C;
    private double MAX_CALCIUM;
    private double MAX_IRON;

    private double MIN_ENERGY_KCAl_TO_REACH;
    private double MIN_FAT_TO_REACH;
    private double MIN_SATURATED_FAT_TO_REACH;
    private double MIN_CARBOHYDRATES_TO_REACH;
    private double MIN_SUGARS_TO_REACH;
    private double MIN_SALT_TO_REACH;


    private double actualEnergyKcal;
    private double actualFat;
    private double actualSaturatedFat;
    private double actualCarbohydrates;
    private double actualSugars;
    private double actualSalt;
    private double actualVitaminA;
    private double actualVitaminC;
    private double actualCalcium;
    private double actualIron;


    public MealGenerator(Dataset<Row> filteredDataset, String DietChoice) {
        this.DietChoice = DietChoice;
        this.filteredDataset = filteredDataset;
        // Récupération des produits pour chaque repas depuis le dataset filtré
        for (Row row : filteredDataset.collectAsList()) {
            Meal meal = new Meal(row);
            // Ajoutez le produit à la liste appropriée en fonction de son type de repas
            if (isBreakfastProduct(meal)) {
                breakfastMeals.add(meal);
            }
            if (isLunchProduct(meal)) {
                lunchMeals.add(meal);
            }
            if (isDinnerProduct(meal)) {
                dinnerMeals.add(meal);
            }
        }

    }


    private void setDailyLimits() {
        switch (DietChoice) {
            case "Vegetarian":
                MAX_ENERGY_KCAL = 2500;
                MAX_FAT = 70;
                MAX_SATURATED_FAT = 20;
                MAX_CARBOHYDRATES = 300;
                MAX_SUGARS = 90;
                MAX_SALT = 6;
//                setMinToBeReach();
                break;
            case "Mediterranean":
                MAX_ENERGY_KCAL = 2500;
                MAX_FAT = 70;
                MAX_SATURATED_FAT = 20;
                MAX_CARBOHYDRATES = 300;
                MAX_SUGARS = 90;
                MAX_SALT = 6;
                MAX_VITAMIN_C = 90; // Niveau élevé en vitamine C
                MAX_CALCIUM = 1000; // Niveau élevé en calcium
                setMinToBeReach();
                break;
            case "Gluten-free":
                MAX_ENERGY_KCAL = 2200;
                MAX_FAT = 60;
                MAX_SATURATED_FAT = 15;
                MAX_CARBOHYDRATES = 300; // Peut être riche en glucides selon les substituts de gluten utilisés
                MAX_SUGARS = 90;
                MAX_SALT = 6;
                setMinToBeReach();
                break;
            case "Ketogenic":
                MAX_ENERGY_KCAL = 1800; // Réduit en raison de la restriction sévère en glucides
                MAX_FAT = 100; // Riche en graisses
                MAX_SATURATED_FAT = 30; // Limite strictement les graisses saturées
                MAX_CARBOHYDRATES = 50; // Très faible en glucides
                MAX_SUGARS = 30; // Limite strictement les sucres
                MAX_SALT = 2; // Limite strictement le sel
                setMinToBeReach();
                break;
            case "DASH":
                MAX_ENERGY_KCAL = 2300;
                MAX_FAT = 70;
                MAX_SATURATED_FAT = 20;
                MAX_CARBOHYDRATES = 300;
                MAX_SUGARS = 90;
                MAX_SALT = 6;
                MAX_VITAMIN_C = 90; // Niveau élevé en vitamine C
                MAX_CALCIUM = 1000; // Niveau élevé en calcium
                setMinToBeReach();
                break;
            case "Balanced":
                MAX_ENERGY_KCAL = 2500;
                MAX_FAT = 70;
                MAX_SATURATED_FAT = 20;
                MAX_CARBOHYDRATES = 300;
                MAX_SUGARS = 90;
                MAX_SALT = 6;
                MAX_VITAMIN_C = 90; // Niveau élevé en vitamine C
                MAX_CALCIUM = 1000; // Niveau élevé en calcium
                setMinToBeReach();
                break;
        }
        resetActualNutri();
    }

    private void resetActualNutri() {
        actualEnergyKcal = 0;
        actualFat = 0;
        actualSaturatedFat = 0;
        actualCarbohydrates = 0;
        actualSugars = 0;
        actualSalt = 0;
        actualVitaminA = 0;
        actualVitaminC = 0;
        actualCalcium = 0;
        actualIron = 0;
    }


    private void setMinToBeReach() {
        MIN_ENERGY_KCAl_TO_REACH = MAX_ENERGY_KCAL/4;
        MIN_FAT_TO_REACH = MAX_FAT/4;
        MIN_SATURATED_FAT_TO_REACH = MAX_SATURATED_FAT/4;
        MIN_CARBOHYDRATES_TO_REACH = MAX_CARBOHYDRATES/4;
        MIN_SUGARS_TO_REACH = MAX_SUGARS/4;
        MIN_SALT_TO_REACH = MAX_SALT/4;
    }

    // Génère un repas pour un moment donné de la journée (petit-déjeuner, déjeuner, dîner)
    private Meal generateMeal(List<Meal> meals) {
        // Prend en compte les limites nutritionnelles=
        List<Meal> possibleMeals = new ArrayList<>();
        for (Meal meal : meals) {
            if (actualEnergyKcal + meal.getEnergyKcal() <= MAX_ENERGY_KCAL
                    && actualFat + meal.getFat() <= MAX_FAT
                    && actualSaturatedFat + meal.getSaturatedFat() <= MAX_SATURATED_FAT
                    && actualCarbohydrates + meal.getCarbohydrates() <= MAX_CARBOHYDRATES
                    && actualSugars + meal.getSugars() <= MAX_SUGARS
                    && actualSalt + meal.getSalt() <= MAX_SALT
                    && actualVitaminA + meal.getVitaminA() <= MAX_VITAMIN_A
                    && actualVitaminC + meal.getVitaminC() <= MAX_VITAMIN_C
                    && actualCalcium + meal.getCalcium() <= MAX_CALCIUM
                    && actualIron + meal.getIron() <= MAX_IRON
                    && actualEnergyKcal + meal.getEnergyKcal() >= MIN_ENERGY_KCAl_TO_REACH
                    && actualFat + meal.getFat() >= MIN_FAT_TO_REACH
                    && actualSaturatedFat + meal.getSaturatedFat() >= MIN_SATURATED_FAT_TO_REACH
                    && actualCarbohydrates + meal.getCarbohydrates() >= MIN_CARBOHYDRATES_TO_REACH
                    && actualSugars + meal.getSugars() >= MIN_SUGARS_TO_REACH
                    && actualSalt + meal.getSalt() >= MIN_SALT_TO_REACH
            ) {
                possibleMeals.add(meal);
            }
        }
        if (possibleMeals.size() > 0) {
            Random random = new Random();
            Meal meal = possibleMeals.get(random.nextInt(possibleMeals.size()));
            updateAndCheckNutritionalValues(meal);
            return meal;
        }
        else {
            System.out.println("Aucun repas ne respecte les limites nutritionnelles");
            return null;
        }
    }


    // Génère les repas pour une journée en respectant les limites nutritionnelles
    public void generateDailyMeals() {
        // On set les limites journalières
        setDailyLimits();
        // Génération du petit-déjeuner
        todayBreakfast = generateMeal(breakfastMeals);

        // Génération du déjeuner
        todayLunch = generateMeal(lunchMeals);


        // Génération du dîners
        todayDinner = generateMeal(dinnerMeals);




    }

    // Ajoute un repas à la liste des repas du jour
    private void addToMealList(Meal meal, List<Meal> mealList) {
        if (meal != null) {
            mealList.add(meal);
            updateNutritionalValues(meal);
        } else {
            mealList.add(new Meal(null));
        }
    }

    // Met à jour les valeurs nutritionnelles actuelles avec les valeurs du repas donné
    private void updateNutritionalValues(Meal meal) {
        if (meal != null) {
            actualEnergyKcal += meal.getEnergyKcal();
            actualFat += meal.getFat();
            actualSaturatedFat += meal.getSaturatedFat();
            actualCarbohydrates += meal.getCarbohydrates();
            actualSugars += meal.getSugars();
            actualSalt += meal.getSalt();
            actualVitaminA += meal.getVitaminA();
            actualVitaminC += meal.getVitaminC();
            actualCalcium += meal.getCalcium();
            actualIron += meal.getIron();
        }
    }

    // Obtient les repas du petit-déjeuner pour la semaine
    public Meal getTodayBreakfast() {
        return todayBreakfast;
    }

    public Meal getTodayLunch() {
        return todayLunch;
    }
    public Meal getTodayDinner() {
        return todayDinner;
    }

    // Vérifie si le produit est un produit de petit-déjeuner
    private boolean isBreakfastProduct(Meal meal) {
        return true;
    }

    // Vérifie si le produit est un produit de déjeuner
    private boolean isLunchProduct(Meal meal) {
        return true;
    }

    // Vérifie si le produit est un produit de dîner
    private boolean isDinnerProduct(Meal meal) {
        return true;
    }

    // Met à jour les valeurs nutritionnelles actuelles avec les valeurs du repas donné et vérifie si elles dépassent les limites
    private void updateAndCheckNutritionalValues(Meal meal) {
        updateNutritionalValues(meal);
    }

}