package edu.northeastern.yummycusine.ui.favourite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.yummycusine.Cuisine;
import edu.northeastern.yummycusine.LoginActivity;
import edu.northeastern.yummycusine.R;
import edu.northeastern.yummycusine.ui.home.RecipeAdapter;


public class FavouriteFragment extends Fragment {

    private RecipeAdapter recipeAdapter;
    private List<Cuisine> likedCuisines;
    private FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        likedCuisines = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if (user == null){view.findViewById(R.id.textViewFavourite).setTooltipText("Empty Favourite. Please login first.");}

        RecyclerView recyclerViewAllRecipes = view.findViewById(R.id.recyclerFavourites);
        recyclerViewAllRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recipeAdapter = new RecipeAdapter(this, likedCuisines);
        recyclerViewAllRecipes.setAdapter(recipeAdapter);

        loadLikedRecipes();

        return view;
    }

    private void loadLikedRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).collection("likedRecipes")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Cuisine cuisine = document.toObject(Cuisine.class);
                                likedCuisines.add(cuisine);
                            }
                            Log.w("LikedRecipesActivity", "liked:" + likedCuisines.toString());
                            recipeAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("LikedRecipesActivity", "Error getting documents.", task.getException());
                        }
                    });
        } else {
            // Prompt user to log in
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            intent.putExtra("parent", "MainActivity");
            startActivity(new Intent(this.getActivity(), LoginActivity.class));
        }
    }

}