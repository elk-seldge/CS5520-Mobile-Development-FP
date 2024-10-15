package edu.northeastern.yummycusine.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.yummycusine.Cuisine;
import edu.northeastern.yummycusine.CuisineAdapter;
import edu.northeastern.yummycusine.R;

public class HomeFragment extends Fragment {
    private RecipePagerAdapter recipePagerAdapter;
    private RecipeAdapter recipeAdapter;
    private List<Cuisine> cuisineList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cuisineList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Initialize ViewPager2 with its adapter
        ViewPager2 viewPagerTopRecipes = view.findViewById(R.id.viewPagerTopRecipes);
        recipePagerAdapter = new RecipePagerAdapter(this, new ArrayList<>());
        viewPagerTopRecipes.setAdapter(recipePagerAdapter);

        // Initialize RecyclerView with its adapter

        RecyclerView recyclerViewAllRecipes = view.findViewById(R.id.recyclerViewAllRecipes);
        recyclerViewAllRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recipeAdapter = new RecipeAdapter(this, new ArrayList<>());
        recyclerViewAllRecipes.setAdapter(recipeAdapter);

        db.collection("recipes").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cuisineList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Cuisine cuisine = document.toObject(Cuisine.class);
                            cuisineList.add(cuisine);
                        }
                        recipeAdapter.updateList(cuisineList);
                        recipePagerAdapter.updateData(cuisineList);
                    } else {
                        Log.w("HomeFragment", "Error getting documents.", task.getException());
                    }
                });

        return view;
    }
}
