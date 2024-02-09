package org.sparkFoodIA;

import org.apache.spark.sql.Row;

public class Meal {
    private String name;
    private String countries;
    private String allergens;
    private String traces;
    private String servingSize;
    private String imageUrl;
    private double energyKj;
    private double energyKcal;
    private double fat;
    private double saturatedFat;
    private double carbohydrates;
    private double sugars;
    private double fiber;
    private double proteins;
    private double salt;
    private double vitaminA;
    private double vitaminC;
    private double calcium;
    private double iron;
    private double productQuantity;
    private String ingredientsAnalysisTags;
    private int dayIndex;

    public Meal(Row row) {
        this.name = row.getString(1);
        this.countries = row.getString(2);
        this.allergens = row.getString(3);
        this.traces = row.getString(4);
        this.servingSize = row.getString(5);
        this.imageUrl = row.getString(6);
        this.energyKj = row.getDouble(7);
        this.energyKcal = row.getDouble(8);
        this.fat = row.getDouble(9);
        this.saturatedFat = row.getDouble(10);
        this.carbohydrates = row.getDouble(11);
        this.sugars = row.getDouble(12);
        this.fiber = row.getDouble(13);
        this.proteins = row.getDouble(14);
        this.salt = row.getDouble(15);
        this.vitaminA = row.getDouble(16);
        this.vitaminC = row.getDouble(17);
        this.calcium = row.getDouble(18);
        this.iron = row.getDouble(19);
        this.productQuantity = row.getDouble(20);
        this.ingredientsAnalysisTags = row.getString(21);

    }

    public String getMealName() {
        return name;
    }

    public double getEnergyKcal() {
        return energyKcal;
    }

    public double getFat() {
        return fat;
    }

    public double getSaturatedFat() {
        return saturatedFat;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getSugars() {
        return sugars;
    }

    public double getSalt() {
        return salt;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getIron() {
        return iron;
    }

    public String toString() {
        return name + " - " + countries + " - " + allergens + " - " + traces + " - " + servingSize + " - " + imageUrl + " - " + energyKj + " - " + energyKcal + " - " + fat + " - " + saturatedFat + " - " + carbohydrates + " - " + sugars + " - " + fiber + " - " + proteins + " - " + salt + " - " + vitaminA + " - " + vitaminC + " - " + calcium + " - " + iron + " - " + productQuantity + " - " + ingredientsAnalysisTags;
    }

    public String getIngredientsAnalysisTags() {
        return ingredientsAnalysisTags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCountries() {
        return countries;
    }

    public String getAllergens() {
        return allergens;
    }

    public String getTraces() {
        return traces;
    }

    public String getServingSize() {
        return servingSize;
    }

    public double getEnergyKj() {
        return energyKj;
    }

    public double getFiber() {
        return fiber;
    }

    public double getProteins() {
        return proteins;
    }

    public double getProductQuantity() {
        return productQuantity;
    }


    public int getDayIndex() {
        return dayIndex;
    }
    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }
}
