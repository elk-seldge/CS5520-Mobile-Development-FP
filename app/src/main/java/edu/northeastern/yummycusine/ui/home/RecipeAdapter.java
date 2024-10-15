package edu.northeastern.yummycusine.ui.home;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.northeastern.yummycusine.Cuisine;
import edu.northeastern.yummycusine.CuisineDetailActivity;
import edu.northeastern.yummycusine.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Fragment fragment;
    private List<Cuisine> cuisines;

    public RecipeAdapter(Fragment fragment, List<Cuisine> cuisines) {
        this.fragment = fragment;
        this.cuisines = cuisines;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuisine, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Cuisine cuisine = cuisines.get(position);
        Log.d("RecyclerView", "Binding view for: " + cuisine.getName());
        holder.name.setText(cuisine.getName());
        holder.ingredients.setText(cuisine.getIngredients());
        Glide.with(fragment).load(cuisine.getImageUrl()).into(holder.image);

        // Set the click listener
        holder.itemView.setOnClickListener(v -> {
            Intent detailIntent = new Intent(fragment.getActivity(), CuisineDetailActivity.class);
            detailIntent.putExtra("name", cuisine.getName());
            detailIntent.putExtra("user", cuisine.getUser());
            detailIntent.putExtra("ingredients", cuisine.getIngredients());
            detailIntent.putExtra("instructions", cuisine.getInstructions());
            detailIntent.putExtra("imageUrl", cuisine.getImageUrl());
            fragment.startActivity(detailIntent);
        });
    }

    @Override
    public int getItemCount() {
        return cuisines.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name, ingredients, user;
        ImageView image;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cuisineName);
            ingredients = itemView.findViewById(R.id.cuisineIngredients);
            image = itemView.findViewById(R.id.cuisineImage);
        }
    }

    public void updateList(List<Cuisine> list) {
        cuisines.clear();
        cuisines.addAll(list);
        notifyDataSetChanged();
    }
}