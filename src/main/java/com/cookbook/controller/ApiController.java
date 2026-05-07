package com.cookbook.controller;

import com.cookbook.model.*;
import com.cookbook.service.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final RecipeService recipeService;
    private final UserService userService;

    @GetMapping("/recipes")
    public ResponseEntity<?> getAllRecipes(@AuthenticationPrincipal UserDetails principal) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        Optional<Recipe> featured = recipeService.getFeaturedRecipe();

        Set<Long> favouriteIds = new HashSet<>();
        boolean isAdmin = false;

        if (principal != null) {
            User user = userService.findByEmail(principal.getUsername()).orElse(null);
            if (user != null) {
                favouriteIds = recipeService.getFavouriteIds(user);
                isAdmin = user.isAdmin();
            }
        }

        return ResponseEntity.ok(Map.of(
                "recipes", buildRecipeDtos(recipes, favouriteIds),
                "featured", featured.map(r -> toDto(r, favouriteIds.contains(r.getId()))).orElse(null),
                "isAdmin", isAdmin
        ));
    }


    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false, defaultValue = "") String query,
                                     @AuthenticationPrincipal UserDetails principal) {
        List<Recipe> results = recipeService.searchByName(query);
        Set<Long> favouriteIds = new HashSet<>();
        if (principal != null) {
            userService.findByEmail(principal.getUsername())
                    .ifPresent(u -> favouriteIds.addAll(recipeService.getFavouriteIds(u)));
        }
        return ResponseEntity.ok(Map.of("recipes", buildRecipeDtos(results, favouriteIds)));
    }


    @GetMapping("/recipes/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails principal) {
        Optional<Recipe> opt = recipeService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Recipe not found."));
        }
        Recipe r = opt.get();
        boolean fav = false;
        if (principal != null) {
            User u = userService.findByEmail(principal.getUsername()).orElse(null);
            if (u != null) fav = recipeService.isFavourite(u, r);
        }
        return ResponseEntity.ok(Map.of("success", true, "recipe", toDto(r, fav)));
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "user", Map.of(
                                "id", u.getId(),
                                "username", u.getUsername(),
                                "email", u.getEmail(),
                                "role", u.getRole()
                        )
                )))
                .orElseGet(() -> ResponseEntity.ok(Map.of("success", false, "message", "User not found.")));
    }


    @PostMapping("/recipe/add")
    public ResponseEntity<?> addRecipe(@RequestParam String title,
                                        @RequestParam String description,
                                        @AuthenticationPrincipal UserDetails principal) {
        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Title and description are required."));
        }
        User user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        recipeService.addRecipe(title, description, user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Recipe added successfully."));
    }


    @PostMapping("/recipe/edit")
    public ResponseEntity<?> editRecipe(@RequestParam Long recipeId,
                                         @RequestParam String recipeName,
                                         @RequestParam(required = false, defaultValue = "") String recipeDescription,
                                         @RequestParam(required = false, defaultValue = "") String recipeIngredients,
                                         @RequestParam(required = false, defaultValue = "") String recipeSteps,
                                         @RequestParam(required = false, defaultValue = "grey.jpg") String recipeImage,
                                         @AuthenticationPrincipal UserDetails principal) {
        try {
            recipeService.updateRecipe(recipeId, recipeName, recipeDescription,
                    recipeIngredients, recipeSteps, recipeImage);
            return ResponseEntity.ok(Map.of("success", true, "message", "Recipe updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @DeleteMapping("/recipe/delete/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        try {
            recipeService.deleteRecipe(id, user);
            return ResponseEntity.ok(Map.of("success", true, "message", "Recipe deleted successfully."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @PostMapping("/favourite/toggle")
    public ResponseEntity<?> toggleFavourite(@RequestBody FavouriteToggleRequest req,
                                              @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "You must be logged in to add to favorites."));
        }
        User user = userService.findByEmail(principal.getUsername()).orElseThrow();
        Recipe recipe = recipeService.findById(req.getRecipeId())
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        boolean added = recipeService.toggleFavourite(user, recipe);
        String msg = added ? "Recipe added to favorites." : "Recipe removed from favorites.";
        return ResponseEntity.ok(Map.of("success", true, "message", msg, "added", added));
    }


    private List<Map<String, Object>> buildRecipeDtos(List<Recipe> recipes, Set<Long> favouriteIds) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Recipe r : recipes) {
            list.add(toDto(r, favouriteIds.contains(r.getId())));
        }
        return list;
    }

    private Map<String, Object> toDto(Recipe r, boolean isFav) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", r.getId());
        map.put("name", r.getName());
        map.put("description", r.getDescription());
        map.put("imageUrl", r.getImageUrl());
        map.put("ingredients", r.getIngredients());
        map.put("steps", r.getSteps());
        map.put("createdAt", r.getCreatedAt());
        map.put("isFavourite", isFav);
        if (r.getAuthor() != null) {
            map.put("authorId", r.getAuthor().getId());
            map.put("authorName", r.getAuthor().getUsername());
        }
        return map;
    }

    @Data
    static class FavouriteToggleRequest {
        private Long recipeId;
        private boolean isFavorited;
    }
}
