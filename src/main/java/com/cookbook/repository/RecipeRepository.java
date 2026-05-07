package com.cookbook.repository;

import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByOrderByCreatedAtDesc();

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByAuthor(User author);

    Optional<Recipe> findFirstByFeatured(Integer featured);

    @Query("SELECT r FROM Recipe r ORDER BY r.createdAt DESC")
    List<Recipe> findAllRecipesOrdered();
}
