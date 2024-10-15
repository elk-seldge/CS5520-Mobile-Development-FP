package edu.northeastern.yummycusine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CuisineAdapter adapter;
    private List<Cuisine> cuisineList;
    private FirebaseFirestore db;
    private void initializeData() {
        recyclerView = findViewById(R.id.recyclerView);
        cuisineList = new ArrayList<>();
        adapter = new CuisineAdapter(this, cuisineList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView2);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void fetchCuisines() {
        db.collection("recipes").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Cuisine cuisine = document.toObject(Cuisine.class);
                            cuisineList.add(cuisine);
                        }
                        adapter.notifyDataSetChanged();
                        Log.w("SearchActivity", cuisineList.toString());
                    } else {
                        Log.w("SearchActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeData();
        fetchCuisines();
        setupSearchView();
    }

    private void filter(String text) {
        List<Cuisine> filteredList = new ArrayList<>();
        for (Cuisine item : cuisineList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getIngredients().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        Log.d("SearchActivity", "Filtered List Size: " + filteredList.size());
        adapter.updateList(filteredList);
    }
}