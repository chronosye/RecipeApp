package com.uldisj.recipeapp.viewmodel

import androidx.lifecycle.*
import com.uldisj.recipeapp.model.database.RecipeRepository
import com.uldisj.recipeapp.model.entities.Recipe
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insertRecipeData(recipe)
    }

    val allRecipesList: LiveData<List<Recipe>> = repository.allRecipesList.asLiveData()

    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.updateRecipeData(recipe)
    }

    val favouriteRecipesList: LiveData<List<Recipe>> = repository.favouriteRecipesList.asLiveData()

    fun delete(recipe: Recipe) = viewModelScope.launch {
        repository.deleteRecipe(recipe)
    }

    fun getFilteredList(value: String): LiveData<List<Recipe>> =
        repository.filteredListRecipes(value).asLiveData()
}

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}