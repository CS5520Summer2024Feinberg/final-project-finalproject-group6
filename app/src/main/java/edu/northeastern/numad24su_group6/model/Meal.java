package edu.northeastern.numad24su_group6.model;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name="Meal Plan")
public class Meal {
    @SmartColumn(id = 0, name = "Food Name")
    private String foodName;

    @SmartColumn(id = 1, name = "Carbs")
    private float carbs;

    @SmartColumn(id = 2, name = "Fats")
    private float fats;

    @SmartColumn(id = 3, name = "Proteins")
    private float proteins;

    @SmartColumn(id = 4, name = "Calories")
    private float calories;

    @SmartColumn(id = 5, name = "Amount")
    private int amount;

    @SmartColumn(id = 6, name = "Sodium")
    private float sodium;

    @SmartColumn(id = 7, name = "Potassium")
    private float potassium;

    @SmartColumn(id = 8, name = "Calcium")
    private float calcium;

    @SmartColumn(id = 9, name = "Iron")
    private float iron;

    @SmartColumn(id = 10, name = "Vitamin A")
    private float vitaminA;

    @SmartColumn(id = 11, name = "Vitamin B")
    private float vitaminB;

    @SmartColumn(id = 12, name = "Vitamin C")
    private float vitaminC;

    @SmartColumn(id = 13, name = "Vitamin D")
    private float vitaminD;

    @SmartColumn(id = 14, name = "Vitamin E")
    private float vitaminE;

    // Constructors, getters and setters
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