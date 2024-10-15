package edu.northeastern.yummycusine;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CuisineAdapter extends RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder> {
    private Context context;
    private List<Cuisine> cuisines;

    public CuisineAdapter(Context context, List<Cuisine> cuisines) {
        this.context = context;
        this.cuisines = cuisines;
    }

    @NonNull
    @Override
    public CuisineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cuisine, parent, false);
        return new CuisineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuisineViewHolder holder, int position) {
        Cuisine cuisine = cuisines.get(position);
        holder.name.setText(cuisine.getName());
        holder.ingredients.setText(cuisine.getIngredients());
        Glide.with(context).load(cuisine.getImageUrl()).into(holder.image);

        // Set the click listener
        holder.itemView.setOnClickListener(v -> {
            Intent detailIntent = new Intent(context, CuisineDetailActivity.class);
            detailIntent.putExtra("name", cuisine.getName());
            detailIntent.putExtra("user", cuisine.getUser());
            detailIntent.putExtra("ingredients", cuisine.getIngredients());
            detailIntent.putExtra("instructions", cuisine.getInstructions());
            detailIntent.putExtra("imageUrl", cuisine.getImageUrl());
            context.startActivity(detailIntent);
        });
    }

    @Override
    public int getItemCount() {
        return cuisines.size();
    }

    static class CuisineViewHolder extends RecyclerView.ViewHolder {
        TextView name, ingredients, user;
        ImageView image;

        public CuisineViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cuisineName);
            ingredients = itemView.findViewById(R.id.cuisineIngredients);
            image = itemView.findViewById(R.id.cuisineImage);
        }
    }

    public void updateList(List<Cuisine> list) {
        cuisines = list;
        notifyDataSetChanged();
    }
}
