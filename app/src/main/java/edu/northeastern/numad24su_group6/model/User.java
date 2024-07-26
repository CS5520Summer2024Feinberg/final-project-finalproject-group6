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
                return dailyCalorieNeeds;
            case "To lose weight":
                return dailyCalorieNeeds * Constants.EIGHTY_FIVE;
            case "To gain weight":
                return dailyCalorieNeeds * Constants.ONE_FIFTEEN;
            default:
                throw new IllegalArgumentException("Invalid goal. Choose from 'maintain', 'lose', or 'gain'.");
        }
    }

    public void calculateMacronutrients() {
        if (dailyCalorieNeeds <= 0) throw new IllegalArgumentException("Daily calorie needs should be a positive value");

        this.carbs = dailyCalorieNeeds * 0.5 / 4; // 50% of calories from carbs, 4 calories per gram
        this.proteins = dailyCalorieNeeds * 0.2 / 4; // 20% of calories from proteins, 4 calories per gram
        this.fats = dailyCalorieNeeds * 0.3 / 9; // 30% of calories from fats, 9 calories per gram
        this.water = weight * 35; // 35 ml per kg of body weight
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

    @Override
    public String toString() {
        if (dailyCalorieNeeds > 0) {
            return "Your estimated daily calorie needs are " + Math.round(dailyCalorieNeeds) + " calories!";
        } else {
            return "Your daily calorie needs are not calculated.";
        }
    }
}