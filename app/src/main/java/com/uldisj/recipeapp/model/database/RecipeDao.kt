package com.uldisj.recipeapp.model.database

import androidx.room.*
import com.uldisj.recipeapp.model.entities.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipeDetails(recipe: Recipe)

    @Query("SELECT * FROM RECIPE_TABLE ORDER BY ID")
    fun getAllRecipesList(): Flow<List<Recipe>>

    @Update
    suspend fun updateRecipeDetails(recipe: Recipe)

    @Query("SELECT * FROM RECIPE_TABLE WHERE favourite_recipe = 1")
    fun getFavouriteRecipesList(): Flow<List<Recipe>>

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM RECIPE_TABLE WHERE type = :filterType")
    fun getFilteredRecipesList(filterType: String): Flow<List<Recipe>>
}