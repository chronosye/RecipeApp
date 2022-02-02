package com.uldisj.recipeapp.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.uldisj.recipeapp.R
import com.uldisj.recipeapp.application.RecipeApplication
import com.uldisj.recipeapp.databinding.FragmentRandomRecipeBinding
import com.uldisj.recipeapp.model.entities.RandomRecipe
import com.uldisj.recipeapp.model.entities.Recipe
import com.uldisj.recipeapp.utils.Constants
import com.uldisj.recipeapp.viewmodel.NotificationsViewModel
import com.uldisj.recipeapp.viewmodel.RandomRecipeViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModelFactory

class RandomRecipeFragment : Fragment() {

    private var _binding: FragmentRandomRecipeBinding? = null

    private lateinit var mRandomRecipeViewModel: RandomRecipeViewModel

    private var mProgressDialog: Dialog? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let{
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideCustomProgressDialog(){
        mProgressDialog?.let{
            it.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomRecipeViewModel = ViewModelProvider(this).get(RandomRecipeViewModel::class.java)

        mRandomRecipeViewModel.getRandomRecipeFromAPI()

        randomRecipeViewModelObserver()

        binding.srlRandomRecipe.setOnRefreshListener {
            mRandomRecipeViewModel.getRandomRecipeFromAPI()
        }
    }

    private fun randomRecipeViewModelObserver() {
        mRandomRecipeViewModel.randomRecipeResponse.observe(
            viewLifecycleOwner
        ) { randomRecipeResponse ->
            randomRecipeResponse?.let {
                if (binding.srlRandomRecipe.isRefreshing) {
                    binding.srlRandomRecipe.isRefreshing = false
                }
                setRandomRecipeResponseInUI(randomRecipeResponse.recipes[0])
            }
        }
        mRandomRecipeViewModel.randomRecipeLoadingError.observe(
            viewLifecycleOwner
        ) { dataError ->
            dataError?.let {
                if (binding.srlRandomRecipe.isRefreshing) {
                    binding.srlRandomRecipe.isRefreshing = false
                }
            }
        }
        mRandomRecipeViewModel.loadRandomRecipe.observe(
            viewLifecycleOwner
        ) { loadRandomRecipe ->
            loadRandomRecipe?.let {
                if(loadRandomRecipe && !binding.srlRandomRecipe.isRefreshing){
                    showCustomProgressDialog()
                }else{
                    hideCustomProgressDialog()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRandomRecipeResponseInUI(recipe: RandomRecipe.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding.ivRecipeImage)

        binding.tvTitle.text = recipe.title

        var recipeType = "Other"
        if (recipe.dishTypes.isNotEmpty()) {
            recipeType = recipe.dishTypes[0]
            binding.tvType.text = recipeType
        }

        binding.tvCategory.text = "Other"

        var ingredients = ""
        for (value in recipe.extendedIngredients) {
            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }

        binding.tvIngredients.text = ingredients


        var instructions = ""
        instructions = Html.fromHtml(
            recipe.instructions,
            Html.FROM_HTML_MODE_COMPACT
        ).toString()

        binding.tvInstructions.text = instructions

        binding.tvCookingTime.text = recipe.readyInMinutes.toString()

        binding.ivFavouriteRecipe.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favourite_button_unselected
            )
        )

        var addedToFavourites = false

        binding.ivFavouriteRecipe.setOnClickListener {
            if (!addedToFavourites) {
                val randomRecipeDetails = Recipe(
                    recipe.image,
                    Constants.RECIPE_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    recipeType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    instructions,
                    true
                )

                val mRecipeViewModel: RecipeViewModel by viewModels {
                    RecipeViewModelFactory((requireActivity().application as RecipeApplication).repository)
                }

                addedToFavourites = true

                mRecipeViewModel.insert(randomRecipeDetails)

                binding.ivFavouriteRecipe.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favourite_button_selected
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}