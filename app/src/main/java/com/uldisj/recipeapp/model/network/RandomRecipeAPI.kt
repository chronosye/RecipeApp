package com.uldisj.recipeapp.model.network

import com.uldisj.recipeapp.model.entities.RandomRecipe
import com.uldisj.recipeapp.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomRecipeAPI {

    @GET(Constants.API_ENDPOINT)
    fun getRandomRecipe(
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,
        @Query(Constants.TAGS) tags: String,
        @Query(Constants.NUMBER) number: Int
    ): Single<RandomRecipe.Recipes>
}