# CookBook — Spring Boot

This is the **PHP → Spring Boot** rewrite of the CookBook recipe web application.

## Tech Stack
| Layer | PHP (original) | Spring Boot (rewrite) |
|---|---|---|
| Language | PHP 8 | Java 17 |
| Framework | Vanilla PHP | Spring Boot 3.2 |
| ORM | Raw `pg_query` | Spring Data JPA / Hibernate |
| Auth | `$_SESSION` | Spring Security (form login) |
| Templates | PHP inline HTML | Thymeleaf |
| Database | PostgreSQL | PostgreSQL (same schema) |

## Project Structure
```
src/main/java/com/cookbook/
├── CookBookApplication.java       # Entry point
├── config/
│   └── SecurityConfig.java        # Spring Security config
├── controller/
│   ├── AuthController.java        # /register
│   ├── PageController.java        # Page routes (/, /recipe, /profile …)
│   └── ApiController.java         # REST API (replaces all php_requests/*.php)
├── model/
│   ├── User.java                  # cooked_users table
│   ├── Recipe.java                # recipes table
│   └── UserFavourite.java         # user_favourite table
├── repository/
│   ├── UserRepository.java
│   ├── RecipeRepository.java
│   └── UserFavouriteRepository.java
├── security/
│   └── CustomUserDetailsService.java
└── service/
    ├── UserService.java
    └── RecipeService.java

src/main/resources/
├── templates/                     # Thymeleaf HTML pages
│   ├── index.html                 # Home page
│   ├── login.html
│   ├── register.html
│   ├── recipe.html                # Recipe detail
│   ├── user-profile.html          # Logged-in user's profile
│   ├── profile.html               # Public profile
│   ├── edit-recipe.html
│   └── about.html
├── static/
│   ├── css/                       # Original CSS files (copied as-is)
│   ├── js/                        # Updated JS files
│   └── images/                    # grey.jpg placeholder
└── application.properties
```

## PHP → Spring Mapping

| PHP file | Spring equivalent |
|---|---|
| `php_requests/login.php` | Spring Security form login (`/login` POST) |
| `php_requests/register.php` | `AuthController.register()` |
| `php_requests/logout.php` | Spring Security logout (`/logout`) |
| `php_requests/getAllRecipes.php` | `GET /api/recipes` |
| `php_requests/getRecipe.php` | `GET /api/recipes/{id}` |
| `php_requests/search.php` | `GET /api/search?query=…` |
| `php_requests/addRecipe.php` | `POST /api/recipe/add` |
| `php_requests/editRecipe.php` | `POST /api/recipe/edit` |
| `php_requests/deleteRecipe.php` | `DELETE /api/recipe/delete/{id}` |
| `php_requests/toggle_favorite.php` | `POST /api/favourite/toggle` |
| `php_requests/getUserProfile.php` | `GET /api/users/{id}` |
| `php/index.php` | `PageController.home()` → `index.html` |
| `php/recipe.php` | `PageController.recipeDetail()` → `recipe.html` |
| `php/user-profile.php` | `PageController.userProfile()` → `user-profile.html` |
| `php/profile.php` | `PageController.publicProfile()` → `profile.html` |
| `php/editRecipeDetails.php` | `PageController.editRecipePage()` → `edit-recipe.html` |
| `php/aboutUs.php` | `PageController.about()` → `about.html` |

## Setup

### 1. Database
The application uses the **same PostgreSQL schema** as the original project.
Run `schema.sql` if starting fresh:
```bash
psql -U postgres -d webFinal -f schema.sql
```

### 2. Configure `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/webFinal
spring.datasource.username=postgres
spring.datasource.password=admin
```

### 3. Build & Run
```bash
mvn clean package
java -jar target/cookbook-1.0.0.jar
```
Or with Maven directly:
```bash
mvn spring-boot:run
```

App runs at **http://localhost:8080**

## Security Notes
- Passwords are stored **plain-text** to match the original PHP app. To upgrade to bcrypt, replace `NoOpPasswordEncoder` in `SecurityConfig` with `BCryptPasswordEncoder` and re-hash all passwords.
- SQL injection is prevented by using JPA/Hibernate parameterised queries throughout (the original PHP had several injection vulnerabilities).
- CSRF protection is enabled for all HTML form submissions (Thymeleaf injects the token automatically).
