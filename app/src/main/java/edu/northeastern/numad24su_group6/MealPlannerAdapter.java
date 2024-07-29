package edu.northeastern.numad24su_group6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad24su_group6.model.Meal;

public class MealPlannerAdapter extends RecyclerView.Adapter<MealPlannerAdapter.MealViewHolder> {

    private List<Meal> mealList;

    public MealPlannerAdapter(List<Meal> mealList) {
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item_table, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.foodNameTextView.setText(meal.getFoodName());
        holder.amountTextView.setText(String.valueOf(meal.getAmount()));
        holder.caloriesTextView.setText(String.valueOf(meal.getCalories()));
        holder.carbsTextView.setText(String.valueOf(meal.getCarbs()));
        holder.fatsTextView.setText(String.valueOf(meal.getFats()));
        holder.proteinsTextView.setText(String.valueOf(meal.getProteins()));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, amountTextView, caloriesTextView, carbsTextView, fatsTextView, proteinsTextView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
            carbsTextView = itemView.findViewById(R.id.carbsTextView);
            fatsTextView = itemView.findViewById(R.id.fatsTextView);
            proteinsTextView = itemView.findViewById(R.id.proteinsTextView);
        }
    }
}