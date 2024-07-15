package edu.northeastern.numad24su_group6.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int MINUS_ONE = -1;
    public static final int MAX_AGE = 150;
    public static final int MAX_HEIGHT = 272;
    public static final int MAX_WEIGHT = 725;
    public static final int MIN_ACTIVITY_LEVEL = 1;
    public static final int MAX_ACTIVITY_LEVEL = 5;

    public static final Map<Integer, Double> ACTIVITY_LEVEL_TO_FACTOR = new HashMap<>();
    static {
        ACTIVITY_LEVEL_TO_FACTOR.put(1, 1.2);
        ACTIVITY_LEVEL_TO_FACTOR.put(2, 1.375);
        ACTIVITY_LEVEL_TO_FACTOR.put(3, 1.55);
        ACTIVITY_LEVEL_TO_FACTOR.put(4, 1.725);
        ACTIVITY_LEVEL_TO_FACTOR.put(5, 1.9);
    }

    // Constants for weight goals
    public static final double EIGHTY_FIVE = 0.85;
    public static final double ONE_FIFTEEN = 1.15;
}