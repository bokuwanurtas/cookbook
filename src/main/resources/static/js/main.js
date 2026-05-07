// main.js – Home page logic for Spring Boot backend

document.addEventListener("DOMContentLoaded", () => {
    loadRecipes();

    const searchBtn = document.getElementById("search_btn");
    const searchInput = document.getElementById("search_input");

    if (searchBtn) {
        searchBtn.addEventListener("click", () => {
            loadRecipes(searchInput ? searchInput.value.trim() : "");
        });
    }
    if (searchInput) {
        searchInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter") loadRecipes(searchInput.value.trim());
        });
    }

    document.addEventListener("click", async (e) => {
        if (e.target.classList.contains("fav-btn")) {
            const btn = e.target;
            await toggleFavourite(btn, parseInt(btn.dataset.recipeId), btn.dataset.isFav === "true");
        }
    });
});

async function loadRecipes(query = "") {
    const url = query ? `/api/search?query=${encodeURIComponent(query)}` : `/api/recipes`;
    try {
        const data = await (await fetch(url)).json();
        if (!query && data.featured) renderFeatured(data.featured);
        renderRecipes(data.recipes || []);
    } catch (err) { console.error(err); }
}

function renderFeatured(r) {
    const el = document.getElementById("featured");
    if (el) el.innerHTML = `<h2>${esc(r.name)}</h2><p>${esc(r.description||"")}</p><a href="/recipe/${r.id}" class="btn btn-success">View Recipe</a>`;
}

function renderRecipes(recipes) {
    const c = document.getElementById("recipe_div");
    if (!c) return;
    if (!recipes.length) { c.innerHTML = "<p>No recipes found.</p>"; return; }
    c.innerHTML = recipes.map(r => {
        const img = r.imageUrl && !r.imageUrl.startsWith("http") ? `/images/${r.imageUrl}` : (r.imageUrl || "/images/grey.jpg");
        return `<div class="col-md-4 mb-4"><div class="card recipe-card h-100">
          <img src="${esc(img)}" alt="${esc(r.name)}" class="card-img-top" style="height:180px;object-fit:cover;" onerror="this.src='/images/grey.jpg'">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">${esc(r.name)}</h5>
            <p class="card-text flex-grow-1">${esc(r.description||"")}</p>
            <div class="d-flex gap-2 mt-2">
              <a href="/recipe/${r.id}" class="btn btn-primary btn-sm">Let's cook</a>
              <button class="btn btn-sm fav-btn ${r.isFavourite?'btn-warning':'btn-outline-warning'}" data-recipe-id="${r.id}" data-is-fav="${r.isFavourite}">★</button>
            </div>
          </div></div></div>`;
    }).join("");
}

async function toggleFavourite(btn, recipeId, currentlyFav) {
    const res = await fetch("/api/favourite/toggle", {
        method: "POST", headers: {"Content-Type":"application/json"},
        body: JSON.stringify({recipeId, isFavorited: currentlyFav})
    });
    if (res.status === 401) { window.location.href = "/login"; return; }
    const data = await res.json();
    if (data.success) {
        btn.dataset.isFav = data.added;
        btn.classList.toggle("btn-warning", data.added);
        btn.classList.toggle("btn-outline-warning", !data.added);
    }
}

function esc(s) {
    return String(s??"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
}
