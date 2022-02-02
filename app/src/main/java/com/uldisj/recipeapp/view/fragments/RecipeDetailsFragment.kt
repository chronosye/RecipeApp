package com.uldisj.recipeapp.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.uldisj.recipeapp.R
import com.uldisj.recipeapp.application.RecipeApplication
import com.uldisj.recipeapp.databinding.FragmentRecipeDetailsBinding
import com.uldisj.recipeapp.model.entities.Recipe
import com.uldisj.recipeapp.utils.Constants
import com.uldisj.recipeapp.viewmodel.RecipeViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModelFactory
import java.io.IOException


class RecipeDetailsFragment : Fragment() {

    private var mRecipeDetails: Recipe? = null

    private var binding: FragmentRecipeDetailsBinding? = null

    private val mRecipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(((requireActivity().application) as RecipeApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share_recipe -> {
                val type = "text/plain"
                val subject = "Check out this recipe!"
                var extraText = ""
                val shareWith = "Share with"

                mRecipeDetails?.let {
                    var image = ""
                    if (it.imageSource == Constants.RECIPE_IMAGE_SOURCE_ONLINE) {
                        image = it.image
                    }
                    val cookingInstructions = it.cookingInstructions

                    extraText = "$image \n" +
                            "\n Title: ${it.title} \n\n Type: ${it.type} \n\n " +
                            "Category: ${it.category}" +
                            "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions " +
                            "To Cook: \n $cookingInstructions" +
                            "\n\n Cooking time" +
                            "${it.cookingTime} minutes."
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT,subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)

                startActivity(Intent.createChooser(intent,shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: RecipeDetailsFragmentArgs by navArgs()

        mRecipeDetails = args.recipeDetails

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.recipeDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("TAG", "Error loading image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.let {
                                Palette.from(resource!!.toBitmap()).generate() { palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    binding!!.ivRecipeImage.setBackgroundColor(intColor)
                                }
                            }
                            return false
                        }

                    })
                    .into(binding!!.ivRecipeImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            binding!!.tvTitle.text = it.recipeDetails.title
            binding!!.tvType.text = it.recipeDetails.type
            binding!!.tvCategory.text = it.recipeDetails.category
            binding!!.tvIngredients.text = it.recipeDetails.ingredients
            binding!!.tvInstructions.text = it.recipeDetails.cookingInstructions
            binding!!.tvCookingTime.text = it.recipeDetails.cookingTime

            if (args.recipeDetails.favouriteRecipe) {
                binding!!.ivFavouriteRecipe.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favourite_button_selected
                    )
                )
            } else {
                binding!!.ivFavouriteRecipe.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favourite_button_unselected
                    )
                )
            }
        }

        binding!!.ivFavouriteRecipe.setOnClickListener {
            args.recipeDetails.favouriteRecipe = !args.recipeDetails.favouriteRecipe

            mRecipeViewModel.update(args.recipeDetails)

            if (args.recipeDetails.favouriteRecipe) {
                binding!!.ivFavouriteRecipe.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favourite_button_selected
                    )
                )
            } else {
                binding!!.ivFavouriteRecipe.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favourite_button_unselected
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}