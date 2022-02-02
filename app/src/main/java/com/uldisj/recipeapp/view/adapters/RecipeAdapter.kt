package com.uldisj.recipeapp.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uldisj.recipeapp.R
import com.uldisj.recipeapp.databinding.ItemRecipeLayoutBinding
import com.uldisj.recipeapp.model.entities.Recipe
import com.uldisj.recipeapp.utils.Constants
import com.uldisj.recipeapp.view.activities.AddUpdateRecipeActivity
import com.uldisj.recipeapp.view.fragments.AllRecipesFragment
import com.uldisj.recipeapp.view.fragments.FavouriteRecipesFragment


class RecipeAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = listOf()

    class ViewHolder(view: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivRecipeImage = view.ivRecipeImage
        val tvTitle = view.tvRecipeTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRecipeLayoutBinding = ItemRecipeLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        Glide.with(fragment)
            .load(recipe.image)
            .into(holder.ivRecipeImage)
        holder.tvTitle.text = recipe.title
        holder.itemView.setOnClickListener {
            if (fragment is AllRecipesFragment) {
                fragment.recipeDetails(recipe)
            }
            if (fragment is FavouriteRecipesFragment) {
                fragment.recipeDetails(recipe)
            }
        }
        holder.ibMore.setOnClickListener {
            val popUp = PopupMenu(fragment.context, holder.ibMore)
            popUp.menuInflater.inflate(R.menu.menu_adapter, popUp.menu)

            popUp.setOnMenuItemClickListener {
                if(it.itemId == R.id.action_edit_recipe){
                    val intent = Intent(fragment.requireActivity(), AddUpdateRecipeActivity::class.java)
                    intent.putExtra(Constants.EXTRA_RECIPE_DETAILS,recipe)
                    fragment.requireActivity().startActivity(intent)

                }else if(it.itemId == R.id.action_delete_recipe){
                    if(fragment is AllRecipesFragment){
                        fragment.deleteRecipe(recipe)
                    }
                }
                true
            }
            popUp.show()
        }

        if(fragment is AllRecipesFragment){
            holder.ibMore.visibility = View.VISIBLE
        }else if(fragment is FavouriteRecipesFragment){
            holder.ibMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun recipeList(list: List<Recipe>) {
        recipes = list
        notifyDataSetChanged()
    }
}