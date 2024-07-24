package edu.northeastern.numad24su_group6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private final List<String> dates;
    private final OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public DateAdapter(List<String> dates, OnItemClickListener onItemClickListener) {
        this.dates = dates;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        holder.dateTextView.setText(dates.get(position));
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            onItemClickListener.onItemClick(dates.get(position));
        });

        // Highlight the selected date
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.circle_background);
        } else {
            holder.itemView.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String date);
    }
}

