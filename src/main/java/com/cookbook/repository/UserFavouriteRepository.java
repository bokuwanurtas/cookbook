package com.cookbook.repository;

import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.cookbook.model.UserFavourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavouriteRepository extends JpaRepository<UserFavourite, Long> {
    Optional<UserFavourite> findByUserAndRecipe(User user, Recipe recipe);
    List<UserFavourite> findByUser(User user);
    boolean existsByUserAndRecipe(User user, Recipe recipe);
}
