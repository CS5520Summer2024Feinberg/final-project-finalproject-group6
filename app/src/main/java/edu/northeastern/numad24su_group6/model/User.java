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

    @Override
    public String toString() {
        if (dailyCalorieNeeds > 0) {
            return "Your estimated daily calorie needs are " + Math.round(dailyCalorieNeeds) + " calories!";
        } else {
            return "Your daily calorie needs are not calculated.";
        }
    }
}
