# MealPlan  

MealPlan est une application Java utilisant Apache Spark pour générer des repas en fonction des préférences alimentaires de l'utilisateur et des données nutritionnelles des produits alimentaires.

## Fonctionnalités

- Génération de repas personnalisés pour une semaine.
- Choix entre différents régimes alimentaires : végétarien, méditerranéen, sans gluten, DASH, etc.
- Filtrage des produits alimentaires en fonction des critères nutritionnels et des préférences alimentaires.
- Affichage des détails nutritionnels des repas générés.

## Configuration requise

- Java JDK 11
- Apache Spark 2.13 - 3
- Maven 3.9.5


## Comment exécuter l'application

Avant de lancer l'application, assurez-vous d'avoir suivi ces étapes de configuration préalable :

1. **Installation de Spark** : Assurez-vous que Spark est installé sur votre machine. Si ce n'est pas le cas, vous pouvez le télécharger à partir du site officiel de Spark : [Apache Spark Downloads](https://spark.apache.org/downloads.html).

2. **Configuration de SPARK_HOME** : Configurez la variable d'environnement `SPARK_HOME` pour pointer vers le répertoire d'installation de Spark.

3. **Ajout du fichier de données** : Ajoutez le fichier de données `products.csv` dans le dossier ressource du projet. Vous pouvez télécharger le fichier à partir de ce lien : [products.csv](https://fr.openfoodfacts.org/data).

   Une fois que vous avez configuré l'environnement, suivez ces étapes pour exécuter l'application :

1. **Construction de l'application** : Exécutez la commande suivante à la racine du projet pour construire l'application avec Maven :
   ```shell
   mvn clean package
    ```
2. **Exécution de l'application** : Exécutez la commande suivante pour lancer l'application :
    ```shell
   spark-submit --class org.sparkFoodIA.Main --master "local[*]" .\target\foodIA-1.0-SNAPSHOT.jar
   ```

## Utilisation

1. Exécutez l'application à l'aide des instructions d'installation.
2. Choisissez votre régime alimentaire préféré parmi les options disponibles.
3. Consultez les détails des repas générés pour chaque jour de la semaine.

## Preview

1. Liste des repas pour chaque jour de la semaine
![image](https://github.com/MathysLioson/MealPlan/assets/77407012/2502e50c-f984-4002-b733-6bbe2afea805)

2. Les caractéristiques nutritionnelles des aliments

![image](https://github.com/MathysLioson/MealPlan/assets/77407012/1e1c5e26-adfc-44b4-bab8-455f717ce3ca)


## Auteurs

- **Mathys LIOSON**

