package com.uldisj.recipeapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.uldisj.recipeapp.application.RecipeApplication
import com.uldisj.recipeapp.databinding.FragmentFavouriteRecipesBinding
import com.uldisj.recipeapp.model.entities.Recipe
import com.uldisj.recipeapp.view.activities.MainActivity
import com.uldisj.recipeapp.view.adapters.RecipeAdapter
import com.uldisj.recipeapp.viewmodel.DashboardViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModelFactory

class FavouriteRecipesFragment : Fragment() {

    private var _binding: FragmentFavouriteRecipesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mRecipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((requireActivity().application as RecipeApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteRecipesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRecipeViewModel.favouriteRecipesList.observe(viewLifecycleOwner) { recipes ->
            recipes.let {
                binding.rvFavouriteRecipeList.layoutManager =
                    GridLayoutManager(requireActivity(), 2)

                val adapter = RecipeAdapter(this)
                binding.rvFavouriteRecipeList.adapter = adapter
                if (it.isNotEmpty()) {
                    binding.rvFavouriteRecipeList.visibility = View.VISIBLE
                    binding.tvNoFavouriteRecipesAdded.visibility = View.GONE
                    adapter.recipeList(it)
                } else {
                    binding.rvFavouriteRecipeList.visibility = View.GONE
                    binding.tvNoFavouriteRecipesAdded.visibility = View.VISIBLE
                }
            }
        }
    }

    fun recipeDetails(recipe: Recipe) {
        findNavController().navigate(
            FavouriteRecipesFragmentDirections.actionFavouriteRecipesToRecipeDetails(
                recipe
            )
        )

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }
}