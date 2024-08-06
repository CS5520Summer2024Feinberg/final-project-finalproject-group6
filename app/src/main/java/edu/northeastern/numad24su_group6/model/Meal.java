package edu.northeastern.numad24su_group6.model;

public class Meal {
    private String foodName;
    private float carbs;
    private float fats;
    private float proteins;
    private float calories;
    private int amount;
    private float sodium;
    private float potassium;
    private float calcium;
    private float iron;
    private float vitaminA;
    private float vitaminB;
    private float vitaminC;
    private float vitaminD;
    private float vitaminE;

    public Meal() {
    }

    public Meal(String foodName, float carbs, float fats, float proteins, float calories, int amount,
                float sodium, float potassium, float calcium, float iron,
                float vitaminA, float vitaminB, float vitaminC, float vitaminD, float vitaminE) {
        this.foodName = foodName;
        this.carbs = carbs;
        this.fats = fats;
        this.proteins = proteins;
        this.calories = calories;
        this.amount = amount;
        this.sodium = sodium;
        this.potassium = potassium;
        this.calcium = calcium;
        this.iron = iron;
        this.vitaminA = vitaminA;
        this.vitaminB = vitaminB;
        this.vitaminC = vitaminC;
        this.vitaminD = vitaminD;
        this.vitaminE = vitaminE;
    }

    // Getters and setters

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getSodium() {
        return sodium;
    }

    public void setSodium(float sodium) {
        this.sodium = sodium;
    }

    public float getPotassium() {
        return potassium;
    }

    public void setPotassium(float potassium) {
        this.potassium = potassium;
    }

    public float getCalcium() {
        return calcium;
    }

    public void setCalcium(float calcium) {
        this.calcium = calcium;
    }

    public float getIron() {
        return iron;
    }

    public void setIron(float iron) {
        this.iron = iron;
    }

    public float getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(float vitaminA) {
        this.vitaminA = vitaminA;
    }

    public float getVitaminB() {
        return vitaminB;
    }

    public void setVitaminB(float vitaminB) {
        this.vitaminB = vitaminB;
    }

    public float getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(float vitaminC) {
        this.vitaminC = vitaminC;
    }

    public float getVitaminD() {
        return vitaminD;
    }

    public void setVitaminD(float vitaminD) {
        this.vitaminD = vitaminD;
    }

    public float getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(float vitaminE) {
        this.vitaminE = vitaminE;
    }
}