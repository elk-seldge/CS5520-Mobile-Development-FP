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
import com.bumptech.glide.annotation.GlideModule;

import java.util.List;

import edu.northeastern.yummycusine.Cuisine;
import edu.northeastern.yummycusine.CuisineDetailActivity;
import edu.northeastern.yummycusine.R;

public class RecipePagerAdapter extends RecyclerView.Adapter<RecipePagerAdapter.ViewHolder> {
    private final List<Cuisine> recipes;
    private Fragment fragment;  // Keep a reference to the fragment for context

    public RecipePagerAdapter(Fragment fragment, List<Cuisine> recipes) {
        this.fragment = fragment;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cuisine cuisine = recipes.get(position);
        Log.d("ViewPager", "Loading item: " + cuisine.getName());
        holder.textViewName.setText(cuisine.getName());
        holder.textViewDescription.setText(cuisine.getIngredients());
        // Use the fragment's context for Glide to handle lifecycle
        Glide.with(fragment).load(cuisine.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent detailIntent = new Intent(fragment.getActivity(), CuisineDetailActivity.class);
            detailIntent.putExtra("name", cuisine.getName());
            detailIntent.putExtra("user", cuisine.getUser());
            detailIntent.putExtra("ingredients", cuisine.getIngredients());
            detailIntent.putExtra("instructions", cuisine.getInstructions());
            detailIntent.putExtra("imageUrl", cuisine.getImageUrl());
            fragment.startActivity(detailIntent);  // Start activity through the fragment
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewDescription, textViewName;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cuisineImage);
            textViewName = itemView.findViewById(R.id.cuisineName);
            textViewDescription = itemView.findViewById(R.id.cuisineIngredients);
        }
    }

    public void updateData(List<Cuisine> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }
}


