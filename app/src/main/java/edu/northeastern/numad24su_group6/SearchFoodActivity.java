package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchFoodActivity extends AppCompatActivity {

    private AutoCompleteTextView etFoodName;
    private Button btnSearch, btnBack;
    private TextView tvPrompt;
    private ImageView ivHintIcon;
    private String username;
    private String userId;

    // Nutritionix API key
    private final String API_KEY = "45b3d6b5fdc3251f247cb56c1d5d8219";
    private final String APP_ID = "05e52e64";

    private static final Map<String, Integer> NUTRIENT_NAME_TO_ATTR_ID = new HashMap<String, Integer>() {{
        put("Energy (Cal)", 208);
        put("Carbohydrate, by difference (g)", 205);
        put("Total lipid (fat) (g)", 204);
        put("Protein (g)", 203);
        put("Sodium, Na (mg)", 307);
        put("Potassium, K (mg)", 306);
        put("Calcium, Ca (mg)", 301);
        put("Iron, Fe (mg)", 303);
        put("Vitamin A, RAE (mcg)", 320);
        put("Thiamin (Vitamin B1) (mg)", 404);
        put("Vitamin C, total ascorbic acid (mg)", 401);
        put("Vitamin D (D2 + D3) (mcg)", 328);
        put("Vitamin E (alpha-tocopherol) (mg)", 323);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        // Initializing AutoCompleteTextView
        etFoodName = findViewById(R.id.etFoodName);

        // Setup the adapter with food suggestions
        String[] foodSuggestions = {"Apple", "Banana", "Chicken", "Date", "Egg", "Fish"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, foodSuggestions);
        etFoodName.setAdapter(adapter);
        etFoodName.setThreshold(1);  // Start suggesting from the first character

        userId = getIntent().getStringExtra("userId");
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        tvPrompt = findViewById(R.id.tvPrompt);
        ivHintIcon = findViewById(R.id.ivHintIcon);

        btnSearch.setOnClickListener(v -> {
            String foodName = etFoodName.getText().toString();
            if (!foodName.isEmpty()) {
                searchFoodInfo(foodName);
            } else {
                Toast.makeText(this, "Please enter a food name.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void searchFoodInfo(String foodName) {
        String url = "https://trackapi.nutritionix.com/v2/natural/nutrients";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("query", foodName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray foods = response.getJSONArray("foods");
                            if (foods.length() > 0) {
                                JSONObject food = foods.getJSONObject(0);

                                String label = food.getString("food_name");
                                double foodCalories = getNutrientInformation(food, "Energy (Cal)");
                                double foodCarbs = getNutrientInformation(food, "Carbohydrate, by difference (g)");
                                double foodFats = getNutrientInformation(food, "Total lipid (fat) (g)");
                                double foodProteins = getNutrientInformation(food, "Protein (g)");
                                double foodSodium = getNutrientInformation(food, "Sodium, Na (mg)");
                                double foodPotassium = getNutrientInformation(food, "Potassium, K (mg)");
                                double foodCalcium = getNutrientInformation(food, "Calcium, Ca (mg)");
                                double foodIron = getNutrientInformation(food, "Iron, Fe (mg)");
                                double foodVitaminA = getNutrientInformation(food, "Vitamin A, RAE (mcg)");
                                double foodVitaminB = getNutrientInformation(food, "Thiamin (Vitamin B1) (mg)");
                                double foodVitaminC = getNutrientInformation(food, "Vitamin C, total ascorbic acid (mg)");
                                double foodVitaminD = getNutrientInformation(food, "Vitamin D (D2 + D3) (mcg)");
                                double foodVitaminE = getNutrientInformation(food, "Vitamin E (alpha-tocopherol) (mg)");

                                String imageUrl = getFoodImage(food);

                                // Pass the food data to the FoodNutritionInfoActivity
                                Intent intent = new Intent(SearchFoodActivity.this, FoodNutritionInfoActivity.class);
                                intent.putExtra("label", label);
                                intent.putExtra("foodCalories", foodCalories);
                                intent.putExtra("foodCarbs", foodCarbs);
                                intent.putExtra("foodFats", foodFats);
                                intent.putExtra("foodProteins", foodProteins);
                                intent.putExtra("foodSodium", foodSodium);
                                intent.putExtra("foodPotassium", foodPotassium);
                                intent.putExtra("foodCalcium", foodCalcium);
                                intent.putExtra("foodIron", foodIron);
                                intent.putExtra("foodVitaminA", foodVitaminA);
                                intent.putExtra("foodVitaminB", foodVitaminB);
                                intent.putExtra("foodVitaminC", foodVitaminC);
                                intent.putExtra("foodVitaminD", foodVitaminD);
                                intent.putExtra("foodVitaminE", foodVitaminE);
                                intent.putExtra("imageUrl", imageUrl);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            } else {
                                showError("No nutrition information found.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("Error parsing food information.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showError("Error retrieving food information.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-app-id", APP_ID);
                headers.put("x-app-key", API_KEY);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private String getFoodImage(JSONObject food) throws JSONException {
        if (food.has("photo")) {
            JSONObject photo = food.getJSONObject("photo");
            if (photo.has("thumb")) {
                return photo.getString("thumb");
            }
        }
        throw new JSONException("Image not found in the food data");
    }

    private double getNutrientInformation(JSONObject food, String nutrientName) throws JSONException {
        JSONArray fullNutrients = food.getJSONArray("full_nutrients");
        for (int i = 0; i < fullNutrients.length(); i++) {
            JSONObject nutrient = fullNutrients.getJSONObject(i);
            if (NUTRIENT_NAME_TO_ATTR_ID.containsKey(nutrientName) &&
                    nutrient.getInt("attr_id") == NUTRIENT_NAME_TO_ATTR_ID.get(nutrientName)) {
                return nutrient.getDouble("value");
            }
        }
        return 0;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}