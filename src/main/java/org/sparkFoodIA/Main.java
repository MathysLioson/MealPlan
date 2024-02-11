    package org.sparkFoodIA;

    import org.apache.spark.sql.Dataset;
    import org.apache.spark.sql.Row;
    import org.apache.spark.sql.SparkSession;

    import javax.swing.*;
    import java.util.*;
    import java.util.List;


    import static org.apache.spark.sql.functions.*;

    public class Main extends JFrame {
        private static String DIETCHOICE;
        public static void main(String[] args) {

            // Récupération du choix de l'utilisateur pour le régime alimentaire
            DIETCHOICE = getDiet();

            // Création de la session Spark
            SparkSession sparkSession = getSparkSession();
            sparkSession.conf().set("spark.sql.debug.maxToStringFields", 10000);
            sparkSession.sparkContext().setLogLevel("ERROR");

            // chargement du fichier CSV
            Dataset<Row> dataset = getRowDataset(sparkSession);

            // Cast des colonnes au type qui nous intéresse
            Dataset<Row> selectedColumns = getRowDataset(dataset);

            System.out.println("Number of lines before clean: " + selectedColumns.count());


            selectedColumns = CleanDataset(selectedColumns);

            // Filtrage des données globales
            Dataset<Row> filteredDataset = getFilteredDataset(selectedColumns);

            System.out.println("Number of lines after the global filter: " + filteredDataset.count());

            // filtrage des données en fonction du régime alimentaire
            filteredDataset = getRowDataset(DIETCHOICE, filteredDataset);

            System.out.println("Number of lines after plan filter: " + filteredDataset.count());

            // Création de l'objet MealGenerator
            MealGenerator mealGenerator = new MealGenerator(filteredDataset, DIETCHOICE);
            List<Meal> breakfastMeals = new ArrayList<>();
            List<Meal> lunchMeals = new ArrayList<>();
            List<Meal> dinnerMeals = new ArrayList<>();
            // Génération des repas
            // affichage des repas pour 7 jours
            System.out.println("Generation of meals for 7 days...");
            for (int i = 0; i < 7; i++) {
                mealGenerator.generateDailyMeals();
                System.out.println("#############################################");
                System.out.println("Day " + (i + 1));
                System.out.println("Breakfast " + mealGenerator.getTodayBreakfast());
                System.out.println("Lunch " + mealGenerator.getTodayLunch());
                System.out.println("Dinner : " + mealGenerator.getTodayDinner());
                System.out.println("#############################################");
                System.out.println();

                breakfastMeals.add(mealGenerator.getTodayBreakfast());
                lunchMeals.add(mealGenerator.getTodayLunch());
                dinnerMeals.add(mealGenerator.getTodayDinner());
                if(mealGenerator.getTodayLunch() != null) {
                    mealGenerator.getTodayLunch().setDayIndex(i);

                }
                if (mealGenerator.getTodayDinner() != null) {
                    mealGenerator.getTodayDinner().setDayIndex(i);
                }
                if (mealGenerator.getTodayBreakfast() != null) {
                    mealGenerator.getTodayBreakfast().setDayIndex(i);
                }

            }

            // enregistrement dans le datawarehouse
            System.out.println("Recording of meals in the datawarehouse...");
            saveMealsInDataWarehouse(breakfastMeals, lunchMeals, dinnerMeals, DIETCHOICE);


            System.out.println("Please wait, displaying meals in a graphic window...");
            // afficher le resultat dans une fenetre graphique
            SwingUtilities.invokeLater(() -> {
                MealGUI gui = new MealGUI(breakfastMeals, lunchMeals, dinnerMeals, DIETCHOICE);
                gui.setVisible(true);
            });


        }

        private static void saveMealsInDataWarehouse(List<Meal> breakfastMeals, List<Meal> lunchMeals, List<Meal> dinnerMeals, String DIETCHOICE) {
            // enregistrement des repas dans un fichier CSV
            System.out.println("Recording of meals in the datawarehouse...");
            MealDataWarehouse mealDataWarehouse = new MealDataWarehouse();
            mealDataWarehouse.saveMealsInCSV(breakfastMeals, "breakfast", DIETCHOICE);
            mealDataWarehouse.saveMealsInCSV(lunchMeals, "lunch", DIETCHOICE);
            mealDataWarehouse.saveMealsInCSV(dinnerMeals, "dinner", DIETCHOICE);

        }


        private static Dataset<Row> getRowDataset(String DietChoice, Dataset<Row> filteredDataset) {
            switch (DietChoice){
                case "Vegetarian":
                    // vegetarian balanced diet
                    filteredDataset = filteredDataset.filter(col("ingredients_analysis_tags").rlike("(?<=,|^)en:vegetarian(?=,|$)"));
                case "Mediterranean":
                    // mediterranean balanced diet
                    break;
                case "Gluten-free":
                    // gluten-free balanced diet
                    filteredDataset = filteredDataset.filter(col("ingredients_analysis_tags").rlike("^(?!.*en:gluten).*$"));
                    break;
                case "Ketogenic":
                    // ketogenic balanced diet
                    break;
                case "DASH":
                    // DASH balanced diet
                    break;
            }
            return filteredDataset;
        }

        private static Dataset<Row> CleanDataset(Dataset<Row> filteredDataset) {
            // replace null with 0.0
            for (String column : filteredDataset.columns()) {
                filteredDataset = filteredDataset.withColumn(column, when(filteredDataset.col(column).isNull(), 0.0).otherwise(filteredDataset.col(column)));
            }
            return filteredDataset;
        }





        private static SparkSession getSparkSession() {
            SparkSession sparkSession = SparkSession.builder()
                    .appName("SparkFoodIA")
                    .master("local[*]")
                    .config("spark.driver.memory", "8g")
                    .getOrCreate();
            return sparkSession;
        }

        private static Dataset<Row> getRowDataset(SparkSession sparkSession) {
            Dataset<Row> dataset = sparkSession.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .option("delimiter", "\t")
                    .csv("src/main/resources/products.csv");
            return dataset;
        }

        private static Dataset<Row> getFilteredDataset(Dataset<Row> selectedColumns) {
            Dataset<Row> filteredDataset = selectedColumns.filter(
                                    col("product_name").isNotNull()
                                    .and(col("countries").like("%FR%")
                                            .or(col("countries").like("%France%"))
                                            .or(col("countries").like("%French%"))
                                            .or(col("countries").like("%Fr%"))
                                            .or(col("countries").like("%fr%"))
                                    )
                                    .and(col("fat_100g").between(0.0, 100.0))
                                    .and(col("saturated-fat_100g").between(0.0, 100.0))
                                    .and(col("carbohydrates_100g").between(0.0, 100.0))
                                    .and(col("sugars_100g").between(0.0, 100.0))
                                    .and(col("sodium_100g").between(0.0,100.0))
                                    .and(col("fiber_100g").between(0.0, 100.0))
                                    .and(col("proteins_100g").between(0.0, 100.0))
                                    .and(col("salt_100g").between(0.0, 100.0))
                                    .and(col("vitamin-a_100g").between(0.0, 100.0))
                                    .and(col("vitamin-c_100g").between(0.0, 100.0))
                                    .and(col("calcium_100g").between(0.0, 100.0))
                                    .and(col("iron_100g").between(0.0, 100.0))
                            );
            return filteredDataset;
        }

        private static Dataset<Row> getRowDataset(Dataset<Row> dataset) {
            // sélection des colonnes
            Dataset<Row> selectedColumns = dataset.select(
                    col("code"),
                    col("product_name").cast("string"),
                    col("countries"),
                    col("allergens"),
                    col("traces"),
                    col("serving_size"),
                    col("image_small_url").cast("string"),
                    col("energy-kj_100g").cast("double"), // Cast en double
                    col("energy-kcal_100g").cast("double"),
                    col("fat_100g").cast("double"),
                    col("saturated-fat_100g").cast("double"),
                    col("carbohydrates_100g").cast("double"),
                    col("sugars_100g").cast("double"),
                    col("fiber_100g").cast("double"),
                    col("proteins_100g").cast("double"),
                    col("salt_100g").cast("double"),
                    col("vitamin-a_100g").cast("double"),
                    col("vitamin-c_100g").cast("double"),
                    col("calcium_100g").cast("double"),
                    col("iron_100g").cast("double"),
                    col("product_quantity").cast("double"),
                    col("ingredients_analysis_tags").cast("string")
            );
            return selectedColumns;
        }

        private static String getDiet() {
            // création d'une fenêtre pour choisir le régime alimentaire
            UserChoiceGUI userChoiceGUI = new UserChoiceGUI();

            while (userChoiceGUI.getDIETCHOICE() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            DIETCHOICE = userChoiceGUI.getDIETCHOICE();
            System.out.println("Diet choice: " + userChoiceGUI.getDIETCHOICE());
            System.out.println("Loading data...");
            return DIETCHOICE;
        }


    }