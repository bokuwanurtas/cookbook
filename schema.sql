-- CookBook PostgreSQL Schema
-- Run this against your PostgreSQL database named "webFinal"

CREATE TABLE IF NOT EXISTS cooked_users (
    user_id       SERIAL PRIMARY KEY,
    user_name     VARCHAR(255) NOT NULL,
    user_email    VARCHAR(255) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL,
    user_role     INTEGER NOT NULL DEFAULT 1  -- 1=user, 2=admin
);

CREATE TABLE IF NOT EXISTS recipes (
    recipe_id          SERIAL PRIMARY KEY,
    recipe_name        VARCHAR(255)   NOT NULL,
    recipe_desc        TEXT,
    recipe_user        INTEGER        REFERENCES cooked_users(user_id) ON DELETE CASCADE,
    recipe_image_url   VARCHAR(512)   DEFAULT 'grey.jpg',
    recipe_ingredients TEXT           DEFAULT '',
    recipe_steps       TEXT           DEFAULT '',
    recipe_featured    INTEGER        DEFAULT 0,
    recipe_created_at  TIMESTAMP      DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_favourite (
    fav_id     SERIAL PRIMARY KEY,
    fav_user   INTEGER NOT NULL REFERENCES cooked_users(user_id)  ON DELETE CASCADE,
    fav_recipe INTEGER NOT NULL REFERENCES recipes(recipe_id)     ON DELETE CASCADE,
    UNIQUE (fav_user, fav_recipe)
);

-- Optional: seed a demo admin user (password: admin123)
-- INSERT INTO cooked_users (user_name, user_email, user_password, user_role)
-- VALUES ('Admin', 'admin@cookbook.com', 'admin123', 2);
