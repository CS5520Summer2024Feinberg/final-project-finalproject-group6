package edu.northeastern.numad24su_group6.model;

import edu.northeastern.numad24su_group6.utils.Constants;

public class User {
    private int age;
    private String sex;
    private double height;
    private double weight;
    private int activityLevel;
    private double basalMetabolicRate;
    private double dailyCalorieNeeds;
    private double carbs;
    private double proteins;
    private double fats;
    private double water;
    private double sodium;
    private double potassium;
    private double calcium;
    private double iron;
    private double vitaminA;
    private double vitaminB;
    private double vitaminC;
    private double vitaminD;
    private double vitaminE;

    private double carbsGoal;
    private double proteinGoal;
    private double fatGoal;
    private double caloriesGoal;
    private double sodiumGoal;
    private double potassiumGoal;
    private double calciumGoal;
    private double ironGoal;
    private double vitaminAGoal;
    private double vitaminBGoal;
    private double vitaminCGoal;
    private double vitaminDGoal;
    private double vitaminEGoal;

    // No-argument constructor
    public User() {
    }

    public User(int age, String sex, double height, double weight, int activityLevel) {
        if (age <= 0 || age > Constants.MAX_AGE) throw new IllegalArgumentException("Invalid age");
        if (!sex.equals("Male") && !sex.equals("Female")) throw new IllegalArgumentException("Invalid sex");
        if (height <= 0 || height > Constants.MAX_HEIGHT) throw new IllegalArgumentException("Invalid height");
        if (weight <= 0 || weight > Constants.MAX_WEIGHT) throw new IllegalArgumentException("Invalid weight");
        if (activityLevel < Constants.MIN_ACTIVITY_LEVEL || activityLevel > Constants.MAX_ACTIVITY_LEVEL) throw new IllegalArgumentException("Invalid activity level");

        this.age = age;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.basalMetabolicRate = Constants.MINUS_ONE;
        this.dailyCalorieNeeds = Constants.MINUS_ONE;
        this.carbs = Constants.MINUS_ONE;
        this.proteins = Constants.MINUS_ONE;
        this.fats = Constants.MINUS_ONE;
        this.water = Constants.MINUS_ONE;
        this.sodium = Constants.MINUS_ONE;
        this.potassium = Constants.MINUS_ONE;
        this.calcium = Constants.MINUS_ONE;
        this.iron = Constants.MINUS_ONE;
        this.vitaminA = Constants.MINUS_ONE;
        this.vitaminB = Constants.MINUS_ONE;
        this.vitaminC = Constants.MINUS_ONE;
        this.vitaminD = Constants.MINUS_ONE;
        this.vitaminE = Constants.MINUS_ONE;
    }

    public void calculateBasalMetabolicRate() {
        if (sex.equals("Male")) {
            basalMetabolicRate = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            basalMetabolicRate = 10 * weight + 6.25 * height - 5 * age - 161;
        }
    }

    public void calculateDailyCalorieNeeds() {
        Double activityFactor = Constants.ACTIVITY_LEVEL_TO_FACTOR.get(activityLevel);
        if (activityFactor == null) throw new IllegalArgumentException("Invalid activity level");

        if (basalMetabolicRate <= 0) throw new IllegalArgumentException("Basal metabolic rate should be positive");

        dailyCalorieNeeds = basalMetabolicRate * activityFactor;
    }

    public double calculateCaloriesGoal(String goal) {
        if (dailyCalorieNeeds <= 0) throw new IllegalArgumentException("Daily calorie needs should be a positive value");
        if (goal == null || (!goal.equals("To maintain weight") && !goal.equals("To lose weight") && !goal.equals("To gain weight"))) {
            throw new IllegalArgumentException("Invalid goal. Choose from 'maintain', 'lose', or 'gain'.");
        }

        switch (goal) {
            case "To maintain weight":
                this.setCaloriesGoal(dailyCalorieNeeds);
                return dailyCalorieNeeds;
            case "To lose weight":
                this.setCaloriesGoal(dailyCalorieNeeds * Constants.EIGHTY_FIVE);
                return dailyCalorieNeeds * Constants.EIGHTY_FIVE;
            case "To gain weight":
                this.setCaloriesGoal(dailyCalorieNeeds * Constants.ONE_FIFTEEN);
                return dailyCalorieNeeds * Constants.ONE_FIFTEEN;
            default:
                throw new IllegalArgumentException("Invalid goal. Choose from 'maintain', 'lose', or 'gain'.");
        }
    }

    public void calculateMacronutrients() {
        if (caloriesGoal <= 0) throw new IllegalArgumentException("Daily calorie needs should be a positive value");

        // Macronutrients
        this.carbs = caloriesGoal * 0.5 / 4; // 50% of calories from carbs, 4 calories per gram
        this.proteins = caloriesGoal * 0.2 / 4; // 20% of calories from proteins, 4 calories per gram
        this.fats = caloriesGoal * 0.3 / 9; // 30% of calories from fats, 9 calories per gram
        this.water = weight * 35; // 35 ml per kg of body weight

        // Micronutrients
        this.sodiumGoal = 2300; // Recommended daily intake for sodium in mg
        this.potassiumGoal = 3500; // Recommended daily intake for potassium in mg
        this.calciumGoal = 1000; // Recommended daily intake for calcium in mg
        this.ironGoal = (sex.equals("Male")) ? 8 : 18; // Recommended daily intake for iron in mg
        this.vitaminAGoal = 900; // Recommended daily intake for vitamin A in mcg
        this.vitaminBGoal = 2.4; // Recommended daily intake for vitamin B12 in mcg
        this.vitaminCGoal = 90; // Recommended daily intake for vitamin C in mg
        this.vitaminDGoal = 20; // Recommended daily intake for vitamin D in mcg
        this.vitaminEGoal = 15; // Recommended daily intake for vitamin E in mg
    }

    // Getters and setters
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProteins() {
        return proteins;
    }

    public double getFats() {
        return fats;
    }

    public double getWater() {
        return water;
    }

    public double getCalories() {
        return dailyCalorieNeeds;
    }

    public double getCarbsGoal() {
        return carbsGoal;
    }

    public void setCarbsGoal(double carbsGoal) {
        this.carbsGoal = carbsGoal;
    }

    public double getProteinGoal() {
        return proteinGoal;
    }

    public void setProteinGoal(double proteinGoal) {
        this.proteinGoal = proteinGoal;
    }

    public double getFatGoal() {
        return fatGoal;
    }

    public void setFatGoal(double fatGoal) {
        this.fatGoal = fatGoal;
    }

    public double getCaloriesGoal() {
        return caloriesGoal;
    }

    public double getWaterGoal() {
        return weight * 35;
    }

    public void setCaloriesGoal(double caloriesGoal) {
        this.caloriesGoal = caloriesGoal;
    }

    public double getSodium() {
        return sodium;
    }

    public double getPotassium() {
        return potassium;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getIron() {
        return iron;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminB() {
        return vitaminB;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public double getSodiumGoal() {
        return sodiumGoal;
    }

    public double getPotassiumGoal() {
        return potassiumGoal;
    }

    public double getCalciumGoal() {
        return calciumGoal;
    }

    public double getIronGoal() {
        return ironGoal;
    }

    public double getVitaminAGoal() {
        return vitaminAGoal;
    }

    public double getVitaminBGoal() {
        return vitaminBGoal;
    }

    public double getVitaminCGoal() {
        return vitaminCGoal;
    }

    public double getVitaminDGoal() {
        return vitaminDGoal;
    }

    public double getVitaminEGoal() {
        return vitaminEGoal;
    }
    @Override
    public String toString() {
        if (dailyCalorieNeeds > 0) {
            return "Your estimated daily calorie needs are " + Math.round(dailyCalorieNeeds) + " calories!";
        } else {
            return "Your daily calorie needs are not calculated.";
        }
    }
}