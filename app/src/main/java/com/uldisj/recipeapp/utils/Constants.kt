package com.uldisj.recipeapp.utils

object Constants {

    const val RECIPE_TYPE = "RecipeType"
    const val RECIPE_CATEGORY = "RecipeCategory"

    const val RECIPE_IMAGE_SOURCE_LOCAL = "Local"
    const val RECIPE_IMAGE_SOURCE_ONLINE = "Online"

    const val EXTRA_RECIPE_DETAILS = "RecipeDetails"

    const val ALL_ITEMS = "All"
    const val FILTER_SELECTION = "FilterSelection"

    const val API_ENDPOINT = "recipes/random"
    const val API_KEY = "apiKey"
    const val LIMIT_LICENSE = "limitLicense"
    const val TAGS = "tags"
    const val NUMBER = "number"

    const val BASE_URL = "https://api.spoonacular.com/"

    const val API_KEY_VALUE = "8f46f4b2104942d2a1fc934f717f3240"
    const val LIMIT_LICENSE_VALUE = true
    const val TAGS_VALUE = ""
    const val NUMBER_VALUE = 1

    const val NOTIFICATION_ID = "Recipe_notification_id"
    const val NOTIFICATION_NAME = "Recipe"
    const val NOTIFICATION_CHANNEL = "Recipe_channel_01"

    fun recipeTypes(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Dinner")
        list.add("Salad")
        list.add("Dessert")
        list.add("Snack")
        list.add("Other")
        return list
    }

    fun recipeCategories(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("Soup")
        list.add("Bakery")
        list.add("Dessert")
        list.add("Drinks")
        list.add("Soup")
        list.add("Wraps")
        list.add("Burger")
        list.add("Chicken")
        list.add("Pork")
        list.add("Other")
        return list
    }


}