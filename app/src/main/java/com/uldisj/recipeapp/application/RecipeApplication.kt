package com.uldisj.recipeapp.application

import android.app.Application
import com.uldisj.recipeapp.model.database.RecipeRepository
import com.uldisj.recipeapp.model.database.RecipeRoomDatabase

class RecipeApplication : Application() {

    private val database by lazy {
        RecipeRoomDatabase.getDatabase(this)
    }

    val repository by lazy {
        RecipeRepository(database.recipeDao())
    }
}