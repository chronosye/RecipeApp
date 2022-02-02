package com.uldisj.recipeapp.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.uldisj.recipeapp.R
import com.uldisj.recipeapp.application.RecipeApplication
import com.uldisj.recipeapp.databinding.ActivityAddUpdateRecipeBinding
import com.uldisj.recipeapp.databinding.DialogCustomImageSelectionBinding
import com.uldisj.recipeapp.databinding.DialogCustomListBinding
import com.uldisj.recipeapp.model.entities.Recipe
import com.uldisj.recipeapp.utils.Constants
import com.uldisj.recipeapp.view.adapters.CustomListItemAdapter
import com.uldisj.recipeapp.viewmodel.RecipeViewModel
import com.uldisj.recipeapp.viewmodel.RecipeViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddUpdateRecipeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateRecipeBinding
    private var mImagePath: String = ""

    private lateinit var mCustomListDialog: Dialog

    private var mRecipeDetails: Recipe? = null

    private val mRecipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((application as RecipeApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_RECIPE_DETAILS)) {
            mRecipeDetails = intent.getParcelableExtra(Constants.EXTRA_RECIPE_DETAILS)
        }

        setUpActionBar()

        mRecipeDetails?.let {
            if (it.id != 0) {
                mImagePath = it.image
                Glide.with(this@AddUpdateRecipeActivity)
                    .load(mImagePath)
                    .into(binding.ivRecipeImage)

                binding.etTitle.setText(it.title)
                binding.etType.setText(it.type)
                binding.etCategory.setText(it.category)
                binding.etIngredients.setText(it.ingredients)
                binding.etCookingTime.setText(it.cookingTime)
                binding.etDirectionToCook.setText(it.cookingInstructions)

                binding.btnAddRecipe.text = "Update recipe"
            }
        }

        binding.ivAddRecipeImage.setOnClickListener(this)
        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.etCookingTime.setOnClickListener(this)
        binding.btnAddRecipe.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarAddRecipeActivity)
        if (mRecipeDetails != null && mRecipeDetails!!.id != 0) {
            supportActionBar?.let {
                it.title = "Edit recipe"
            }
        } else {
            supportActionBar?.let {
                it.title = "Add recipe"
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddRecipeActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_recipe_image -> {
                    customImageSelectionDialog()
                    return
                }
                R.id.et_type -> {
                    customItemsListDialog(
                        "Select a type",
                        Constants.recipeTypes(),
                        Constants.RECIPE_TYPE
                    )
                    return
                }
                R.id.et_category -> {
                    customItemsListDialog(
                        "Select a category",
                        Constants.recipeCategories(),
                        Constants.RECIPE_CATEGORY
                    )
                    return
                }
                R.id.btn_add_recipe -> {
                    val title = binding.etTitle.text.toString().trim { it <= ' ' }
                    val type = binding.etType.text.toString()
                    val category = binding.etCategory.text.toString()
                    val ingredients = binding.etIngredients.text.toString()
                    val cookingTime = binding.etCookingTime.text.toString()
                    val cookingInstructions = binding.etDirectionToCook.text.toString()

                    when {
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please select image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please enter title",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please enter type",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please enter category",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please enter ingredients",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(cookingTime) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please enter cooking time",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(cookingInstructions) -> {
                            Toast.makeText(
                                this@AddUpdateRecipeActivity,
                                "Please enter cooking instructions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            var recipeID = 0
                            var imageSource = Constants.RECIPE_IMAGE_SOURCE_LOCAL
                            var favouriteRecipe = false

                            mRecipeDetails?.let {
                                if(it.id!=0){
                                    recipeID = it.id
                                    imageSource = it.imageSource
                                    favouriteRecipe = it.favouriteRecipe
                                }
                            }

                            val recipeDetails = Recipe(
                                mImagePath,
                                imageSource,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTime,
                                cookingInstructions,
                                favouriteRecipe,
                                recipeID
                            )

                            if(recipeID == 0){
                                mRecipeViewModel.insert(recipeDetails)
                                Toast.makeText(
                                    this@AddUpdateRecipeActivity,
                                    "You added your recipe!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{
                                mRecipeViewModel.update(recipeDetails)
                                Toast.makeText(
                                    this@AddUpdateRecipeActivity,
                                    "You edited your recipe!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val dialogBinding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startForResultToLoadImageCamera.launch(intent)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }
            ).onSameThread().check()
            dialog.dismiss()
        }

        dialogBinding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startForResultToLoadImageGallery.launch(intent)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@AddUpdateRecipeActivity,
                            "Permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }
            ).onSameThread().check()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions")
            .setPositiveButton("Go to Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private val startForResultToLoadImageCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data.let {
                    val thumbnail: Bitmap = result.data!!.extras!!.get("data") as Bitmap

                    Glide.with(this)
                        .load(thumbnail)
                        .into(binding.ivRecipeImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)

                    binding.ivAddRecipeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
        }

    private val startForResultToLoadImageGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data.let {
                    val selectedPhotoUri = result.data!!.data

                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                }
                                return false
                            }

                        })
                        .into(binding.ivRecipeImage)

                    binding.ivAddRecipeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
        }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>, selection: String) {
        mCustomListDialog = Dialog(this)
        val dialogBinding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(dialogBinding.root)

        dialogBinding.tvTitle.text = title
        dialogBinding.rvList.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this, null, itemsList, selection)
        dialogBinding.rvList.adapter = adapter
        mCustomListDialog.show()

    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            Constants.RECIPE_TYPE -> {
                mCustomListDialog.dismiss()
                binding.etType.setText(item)
            }
            Constants.RECIPE_CATEGORY -> {
                mCustomListDialog.dismiss()
                binding.etCategory.setText(item)
            }
        }
    }

    companion object {
        private const val IMAGE_DIRECTORY = "RecipeAppImages"
    }
}