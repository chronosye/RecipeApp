package com.uldisj.recipeapp.model.network

import com.uldisj.recipeapp.model.entities.RandomRecipe
import com.uldisj.recipeapp.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RandomRecipeApiService {
    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomRecipeAPI::class.java)

    fun getRandomRecipe(): Single<RandomRecipe.Recipes> {
        return api.getRandomRecipe(
            Constants.API_KEY_VALUE,
            Constants.LIMIT_LICENSE_VALUE,
            Constants.TAGS_VALUE,
            Constants.NUMBER_VALUE
        )
    }
}