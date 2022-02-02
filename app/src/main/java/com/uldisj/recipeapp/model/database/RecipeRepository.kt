package com.uldisj.recipeapp.model.database

import androidx.annotation.WorkerThread
import com.uldisj.recipeapp.model.entities.Recipe
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {

    @WorkerThread
    suspend fun insertRecipeData(recipe: Recipe) {
        recipeDao.insertRecipeDetails(recipe)
    }

    val allRecipesList: Flow<List<Recipe>> = recipeDao.getAllRecipesList()

    @WorkerThread
    suspend fun updateRecipeData(recipe: Recipe) {
        recipeDao.updateRecipeDetails(recipe)
    }

    val favouriteRecipesList: Flow<List<Recipe>> = recipeDao.getFavouriteRecipesList()

    @WorkerThread
    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    fun filteredListRecipes(value: String): Flow<List<Recipe>> =
        recipeDao.getFilteredRecipesList(value)
}