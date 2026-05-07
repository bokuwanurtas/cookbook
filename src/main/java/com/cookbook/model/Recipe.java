package com.cookbook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @Column(name = "recipe_name", nullable = false)
    private String name;

    @Column(name = "recipe_desc", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_user")
    private User author;

    @Column(name = "recipe_image_url")
    private String imageUrl = "grey.jpg";

    @Column(name = "recipe_ingredients", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "recipe_steps", columnDefinition = "TEXT")
    private String steps;

    @Column(name = "recipe_featured")
    private Integer featured = 0;

    @Column(name = "recipe_created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserFavourite> favouritedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
