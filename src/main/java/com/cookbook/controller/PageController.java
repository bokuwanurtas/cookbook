package com.cookbook.controller;

import com.cookbook.model.*;
import com.cookbook.repository.UserRepository;
import com.cookbook.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        Optional<Recipe> featured = recipeService.getFeaturedRecipe();

        model.addAttribute("recipes", recipes);
        featured.ifPresent(f -> model.addAttribute("featured", f));

        if (principal != null) {
            userService.findByEmail(principal.getUsername()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("favouriteIds", recipeService.getFavouriteIds(user));
            });
        }
        return "index";
    }


    @GetMapping("/recipe/{id}")
    public String recipeDetail(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails principal,
                                Model model) {
        Recipe recipe = recipeService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        model.addAttribute("recipe", recipe);

        if (principal != null) {
            userService.findByEmail(principal.getUsername()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("isFavourite", recipeService.isFavourite(user, recipe));
            });
        }
        return "recipe";
    }


    @GetMapping("/user-profile")
    public String userProfile(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByEmail(principal.getUsername())
                .orElseThrow();
        List<Recipe> recipes = recipeService.getRecipesByUser(user);
        model.addAttribute("currentUser", user);
        model.addAttribute("recipes", recipes);
        return "user-profile";
    }


    @GetMapping("/profile/{userId}")
    public String publicProfile(@PathVariable Long userId,
                                 @AuthenticationPrincipal UserDetails principal,
                                 Model model) {
        User profileUser = userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        List<Recipe> recipes = recipeService.getRecipesByUser(profileUser);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("recipes", recipes);

        if (principal != null) {
            userService.findByEmail(principal.getUsername())
                    .ifPresent(u -> model.addAttribute("currentUser", u));
        }
        return "profile";
    }


    @GetMapping("/edit-recipe/{id}")
    public String editRecipePage(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails principal,
                                  Model model) {
        Recipe recipe = recipeService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        User user = userService.findByEmail(principal.getUsername()).orElseThrow();

        if (!recipe.getAuthor().getId().equals(user.getId()) && !user.isAdmin()) {
            return "redirect:/";
        }
        model.addAttribute("recipe", recipe);
        model.addAttribute("currentUser", user);
        return "edit-recipe";
    }


    @GetMapping("/about")
    public String about(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findByEmail(principal.getUsername())
                    .ifPresent(u -> model.addAttribute("currentUser", u));
        }
        return "about";
    }
}
