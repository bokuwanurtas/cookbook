package com.cookbook.service;

import com.cookbook.model.*;
import com.cookbook.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserFavouriteRepository favouriteRepository;


    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Recipe> searchByName(String query) {
        if (query == null || query.isBlank()) return getAllRecipes();
        return recipeRepository.findByNameContainingIgnoreCase(query);
    }

    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    public Optional<Recipe> getFeaturedRecipe() {
        return recipeRepository.findFirstByFeatured(1);
    }

    public List<Recipe> getRecipesByUser(User user) {
        return recipeRepository.findByAuthor(user);
    }


    public Set<Long> getFavouriteIds(User user) {
        Set<Long> ids = new HashSet<>();
        favouriteRepository.findByUser(user)
                .forEach(f -> ids.add(f.getRecipe().getId()));
        return ids;
    }

    public boolean isFavourite(User user, Recipe recipe) {
        return favouriteRepository.existsByUserAndRecipe(user, recipe);
    }


    @Transactional
    public Recipe addRecipe(String title, String description, User author) {
        Recipe r = new Recipe();
        r.setName(title);
        r.setDescription(description);
        r.setAuthor(author);
        r.setImageUrl("grey.jpg");
        r.setIngredients("");
        r.setSteps("");
        return recipeRepository.save(r);
    }

    @Transactional
    public Recipe updateRecipe(Long recipeId, String name, String description,
                                String ingredients, String steps, String imageUrl) {
        Recipe r = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        r.setName(name);
        r.setDescription(description);
        r.setIngredients(ingredients);
        r.setSteps(steps);
        r.setImageUrl(imageUrl);
        return recipeRepository.save(r);
    }

    @Transactional
    public void deleteRecipe(Long recipeId, User currentUser) {
        Recipe r = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        boolean isOwner = r.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.isAdmin();
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Not authorized to delete this recipe.");
        }
        recipeRepository.delete(r);
    }

    @Transactional
    public boolean toggleFavourite(User user, Recipe recipe) {
        Optional<UserFavourite> existing = favouriteRepository.findByUserAndRecipe(user, recipe);
        if (existing.isPresent()) {
            favouriteRepository.delete(existing.get());
            return false; // removed
        } else {
            UserFavourite fav = new UserFavourite();
            fav.setUser(user);
            fav.setRecipe(recipe);
            favouriteRepository.save(fav);
            return true; // added
        }
    }
}
