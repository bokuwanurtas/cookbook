package com.cookbook.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
class LoginRequest {
    private String email;
    private String password;
}

@Data
class RegisterRequest {
    private String name;
    private String email;
    private String password;
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String ingredients;
    private String steps;
    private Long authorId;
    private String authorName;
    private boolean favourite;
}

@Data
class RecipeCreateRequest {
    private String title;
    private String description;
}

@Data
class RecipeEditRequest {
    private Long recipeId;
    private String recipeName;
    private String recipeDescription;
    private String recipeIngredients;
    private String recipeSteps;
    private String recipeImage;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ApiResponse {
    private boolean success;
    private String message;
}

@Data
class FavouriteToggleRequest {
    private Long recipeId;
    private boolean isFavorited;
}
