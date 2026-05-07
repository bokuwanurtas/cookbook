// userProfile.js – User profile page for Spring Boot

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".delete-btn").forEach(btn => {
        btn.addEventListener("click", async () => {
            if (!confirm("Delete this recipe?")) return;
            const recipeId = btn.dataset.recipeId;
            const res = await fetch(`/api/recipe/delete/${recipeId}`, {method: "DELETE"});
            const data = await res.json();
            if (data.success) btn.closest(".col-md-4").remove();
            else alert(data.message);
        });
    });
});
