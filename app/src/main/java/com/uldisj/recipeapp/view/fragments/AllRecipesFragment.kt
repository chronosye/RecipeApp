package com.uldisj.recipeapp.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.uldisj.recipeapp.R
import com.uldisj.recipeapp.application.RecipeApplication
import com.uldisj.recipeapp.databinding.DialogCustomListBinding
import com.uldisj.recipeapp.databinding.FragmentAllRecipesBinding
import com.uldisj.recipeapp.model.entities.Recipe
import com.uldisj.recipeapp.utils.Constants
import com.uldisj.recipeapp.view.activities.AddUpdateRecipeActivity
import com.uldisj.recipeapp.view.activities.MainActivity
import com.uldisj.recipeapp.view.adapters.CustomListItemAdapter
import com.uldisj.recipeapp.view.adapters.RecipeAdapter
import com.uldisj.recipeapp.viewmodel.RecipeViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModelFactory

class AllRecipesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllRecipesBinding

    private lateinit var mRecipesAdapter: RecipeAdapter

    private lateinit var mCustomListDialog: Dialog

    private val mRecipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((requireActivity().application as RecipeApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAllRecipesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvRecipeList.layoutManager = GridLayoutManager(requireActivity(), 2)
        mRecipesAdapter = RecipeAdapter(this@AllRecipesFragment)
        mBinding.rvRecipeList.adapter = mRecipesAdapter

        mRecipeViewModel.allRecipesList.observe(viewLifecycleOwner) { recipes ->
            recipes.let {
                if (it.isNotEmpty()) {
                    mBinding.rvRecipeList.visibility = View.VISIBLE
                    mBinding.tvNoRecipesAdded.visibility = View.GONE

                    mRecipesAdapter.recipeList(it)
                } else {
                    mBinding.rvRecipeList.visibility = View.GONE
                    mBinding.tvNoRecipesAdded.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_recipes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_recipe -> {
                startActivity(Intent(requireActivity(), AddUpdateRecipeActivity::class.java))
                return true
            }
            R.id.action_filter_list -> {
                filterRecipesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun recipeDetails(recipe: Recipe) {
        findNavController().navigate(
            AllRecipesFragmentDirections.actionAllRecipesToRecipeDetails(
                recipe
            )
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete recipe")
        builder.setMessage("Are you sure you want to delete this recipe - ${recipe.title}?")
        builder.setIcon(R.drawable.ic_baseline_delete_24)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            mRecipeViewModel.delete(recipe)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterRecipesListDialog() {
        mCustomListDialog = Dialog(requireActivity())
        val dialogBinding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(dialogBinding.root)
        dialogBinding.tvTitle.text = "Select item to filter"
        val recipeTypes = Constants.recipeTypes()
        recipeTypes.add(0, Constants.ALL_ITEMS)
        dialogBinding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        val adapter =
            CustomListItemAdapter(requireActivity(), this, recipeTypes, Constants.FILTER_SELECTION)

        dialogBinding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()

        if (filterItemSelection == Constants.ALL_ITEMS) {
            mRecipeViewModel.allRecipesList.observe(viewLifecycleOwner) { recipes ->
                recipes.let {
                    if (it.isNotEmpty()) {
                        mBinding.rvRecipeList.visibility = View.VISIBLE
                        mBinding.tvNoRecipesAdded.visibility = View.GONE

                        mRecipesAdapter.recipeList(it)
                    } else {
                        mBinding.rvRecipeList.visibility = View.GONE
                        mBinding.tvNoRecipesAdded.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            mRecipeViewModel.getFilteredList(filterItemSelection)
                .observe(viewLifecycleOwner) { recipes ->
                    recipes.let {
                        if (it.isNotEmpty()) {
                            mBinding.rvRecipeList.visibility = View.VISIBLE
                            mBinding.tvNoRecipesAdded.visibility = View.GONE

                            mRecipesAdapter.recipeList(it)
                        }else{
                            mBinding.rvRecipeList.visibility = View.GONE
                            mBinding.tvNoRecipesAdded.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }
}