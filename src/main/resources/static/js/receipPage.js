// receipPage.js – Recipe detail page for Spring Boot

document.addEventListener("DOMContentLoaded", () => {
    const favBtn = document.getElementById("fav-btn");
    if (favBtn) {
        favBtn.addEventListener("click", async () => {
            const recipeId = parseInt(favBtn.dataset.recipeId);
            const isFav = favBtn.dataset.isFav === "true";
            const res = await fetch("/api/favourite/toggle", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({recipeId, isFavorited: isFav})
            });
            if (res.status === 401) { window.location.href = "/login"; return; }
            const data = await res.json();
            if (data.success) {
                favBtn.dataset.isFav = data.added;
                favBtn.classList.toggle("btn-warning", data.added);
                favBtn.classList.toggle("btn-outline-warning", !data.added);
                favBtn.textContent = data.added ? "★ Favourited" : "☆ Add to Favourites";
            }
        });
    }

    const deleteBtn = document.getElementById("delete-btn");
    if (deleteBtn) {
        deleteBtn.addEventListener("click", async () => {
            if (!confirm("Delete this recipe?")) return;
            const recipeId = deleteBtn.dataset.recipeId;
            const res = await fetch(`/api/recipe/delete/${recipeId}`, {method: "DELETE"});
            const data = await res.json();
            if (data.success) window.location.href = "/";
            else alert(data.message);
        });
    }
});
