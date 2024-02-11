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

            System.out.println("Nombre de lignes avant clean: " + selectedColumns.count());


            selectedColumns = CleanDataset(selectedColumns);

            // Filtrage des données globales
            Dataset<Row> filteredDataset = getFilteredDataset(selectedColumns);

            System.out.println("Nombre de lignes après le filtre globale: " + filteredDataset.count());

            // filtrage des données en fonction du régime alimentaire
            filteredDataset = getRowDataset(DIETCHOICE, filteredDataset);

            System.out.println("Nombre de lignes après filtre du règime: " + filteredDataset.count());

            // Création de l'objet MealGenerator
            MealGenerator mealGenerator = new MealGenerator(filteredDataset, DIETCHOICE);
            List<Meal> breakfastMeals = new ArrayList<>();
            List<Meal> lunchMeals = new ArrayList<>();
            List<Meal> dinnerMeals = new ArrayList<>();
            // Génération des repas
            // affichage des repas pour 7 jours

            System.out.println("Génération des repas pour 7 jours...");
            for (int i = 0; i < 7; i++) {
                mealGenerator.generateDailyMeals();
                System.out.println("#############################################");
                System.out.println("Jour " + (i + 1));
                System.out.println("Petit-déjeuner : " + mealGenerator.getTodayBreakfast());
                System.out.println("Déjeuner : " + mealGenerator.getTodayLunch());
                System.out.println("Dîner : " + mealGenerator.getTodayDinner());
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
            System.out.println("Enregistrement des repas dans le datawarehouse...");
            saveMealsInDataWarehouse(breakfastMeals, lunchMeals, dinnerMeals, DIETCHOICE);


            System.out.println("Veuiilez patienter, affichage des repas dans une fenêtre graphique...");
            // afficher le resultat dans une fenetre graphique
            SwingUtilities.invokeLater(() -> {
                MealGUI gui = new MealGUI(breakfastMeals, lunchMeals, dinnerMeals, DIETCHOICE);
                gui.setVisible(true);
            });


        }

        private static void saveMealsInDataWarehouse(List<Meal> breakfastMeals, List<Meal> lunchMeals, List<Meal> dinnerMeals, String DIETCHOICE) {
            // enregistrement des repas dans un fichier CSV
            System.out.println("Enregistrement des repas dans un fichier CSV...");
            MealDataWarehouse mealDataWarehouse = new MealDataWarehouse();
            mealDataWarehouse.saveMealsInCSV(breakfastMeals, "breakfast", DIETCHOICE);
            mealDataWarehouse.saveMealsInCSV(lunchMeals, "lunch", DIETCHOICE);
            mealDataWarehouse.saveMealsInCSV(dinnerMeals, "dinner", DIETCHOICE);

        }


        private static Dataset<Row> getRowDataset(String DietChoice, Dataset<Row> filteredDataset) {
            switch (DietChoice){
                case "Vegetarian":
                    // vegetarian balanced diet
                    filteredDataset = filteredDataset.filter(
                            col("product_name").isNotNull()
                                    .and(col("ingredients_analysis_tags").rlike("(?<=,|^)en:vegetarian(?=,|$)")));
                case "Mediterranean":
                    // mediterranean balanced diet
                    break;
                case "Gluten-free":
                    // gluten-free balanced diet
                    filteredDataset = filteredDataset.filter(
                            col("product_name").isNotNull()
                                    .and(col("ingredients_analysis_tags").ilike("%gluten-free%"))
                    );
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
            switch (userChoice()){
                case 1 :
                    DIETCHOICE = "Vegetarian";
                    break;
                case 2 :
                    DIETCHOICE = "Mediterranean";
                    break;
                case 3 :
                    DIETCHOICE = "Gluten-free";
                    break;
                case 4 :
                    DIETCHOICE = "Ketogenic";
                    break;
                case 5 :
                    DIETCHOICE = "DASH";
                    break;
                case 6 :
                    DIETCHOICE = "Balanced";
                    break;
                case 7 :
                    DIETCHOICE = "Exit";
                    break;
                default :
                    DIETCHOICE = "Invalid choice";
                    break;

            }
            return DIETCHOICE;
        }

        private static int userChoice() {
            Scanner sc = new Scanner(System.in);
            int choice = 0;
            System.out.println("Please choose your diet: ");
            System.out.println("1. Vegetarian");
            System.out.println("2. Mediterranean");
            System.out.println("3. Gluten-free");
            System.out.println("4. Ketogenic");
            System.out.println("5. DASH");
            System.out.println("6. Balanced");
            System.out.println("7. Exit");
            choice = sc.nextInt();
            return choice;
        }


    }