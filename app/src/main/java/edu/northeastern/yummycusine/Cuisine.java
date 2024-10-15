package edu.northeastern.yummycusine;

public class Cuisine {
    private String name;
    private String user;
    private String ingredients;
    private String instructions;
    private String imageUrl;

    public Cuisine() {
        // Default constructor required for calls to DataSnapshot.getValue(Cuisine.class)
    }

    public Cuisine(String name, String user, String ingredients, String instructions, String imageUrl) {
        this.name = name;
        this.user = user;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getUser() {
        return user;
    }
}