package edu.northeastern.numad24su_group6.model;

public class Meal {
    private String foodName;
    private float carbs;
    private float fats;
    private float proteins;
    private float calories;
    private int amount;

    public Meal() {
    }

    public Meal(String foodName, float carbs, float fats, float proteins, float calories, int amount) {
        this.foodName = foodName;
        this.carbs = carbs;
        this.fats = fats;
        this.proteins = proteins;
        this.calories = calories;
        this.amount = amount;
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
}