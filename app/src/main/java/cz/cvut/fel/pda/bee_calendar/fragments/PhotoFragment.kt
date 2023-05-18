package cz.cvut.fel.pda.bee_calendar.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.activities.ProfileActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentPersonalInfoBinding
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentPhotoBinding
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.utils.BitmapConverter
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

class PhotoFragment: Fragment() {
    private lateinit var binding: FragmentPhotoBinding
    private lateinit var user: User
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val PICK_IMAGE = 1002
    var vFilename: String = ""
    var pathImg: String = ""
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var registerActivityTakeAnImage: ActivityResultLauncher<Intent>
    private lateinit var registerPermissionsActivityTakeAnImage: ActivityResultLauncher<String>
    private lateinit var registerActivityBrowseAnImage: ActivityResultLauncher<Intent>
    private lateinit var registerPermissionsActivityBrowseAnImage: ActivityResultLauncher<String>

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);

        (activity as ProfileActivity).supportActionBar?.title = "PHOTO"

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(cz.cvut.fel.pda.bee_calendar.R.id.navigation_save).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        user = userViewModel.loggedUser!!

        registerResultLaunchers()
        loadUserProfileImage()
        uploadProfileImageListener()
    }

    private fun registerResultLaunchers() {
        registerActivityTakeAnImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    val image = it.data?.extras?.get("data") as Bitmap
                    saveAndLoadProfilePicture(image)
                    loadUserProfileImage()
                }
            }
        }

        registerPermissionsActivityTakeAnImage = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takeAnImage()
            } else {
                Toast.makeText(requireActivity(), "Camera permissions are required.", Toast.LENGTH_SHORT).show()
            }
        }

        registerActivityBrowseAnImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val dataUri = it.data?.data!!

                CoroutineScope(Dispatchers.IO).launch {
                    val image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, dataUri))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, dataUri)
                    }
                    saveAndLoadProfilePicture(image)
                }
            }
        }

        registerPermissionsActivityBrowseAnImage = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                browseAnImage()
            } else {
                Toast.makeText(requireActivity(), "Storage reading permissions are required.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun takeAnImageListener() {
        binding.takePhoto.setOnClickListener {
            println("here------------------ take")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takeAnImage()
            } else {
                registerPermissionsActivityTakeAnImage.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takeAnImage() {
        try {
            registerActivityTakeAnImage.launch(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            )
        } catch (e: ActivityNotFoundException) {
            println("Log" + "Activity not found")
        }
    }

    private fun browseAnImageListener() {
        binding.choosePhoto.setOnClickListener {

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                browseAnImage()
            } else {
                registerPermissionsActivityBrowseAnImage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun browseAnImage() {
        try {
            registerActivityBrowseAnImage.launch(
                Intent(ACTION_PICK, EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
            )
        } catch (e: ActivityNotFoundException) {
            println("Log" + "Activity not found")
        }
    }

    private suspend fun saveAndLoadProfilePicture(picture: Bitmap) {
        withContext(Dispatchers.Main) {
            userViewModel.loggedUser!!.profileImg = BitmapConverter.convert(picture)
            userViewModel.updateUser()
            loadUserProfileImage()
        }
    }

    private fun loadUserProfileImage() {
        userViewModel.loggedUser!!.profileImg?.apply {
            BitmapConverter.convert(this).apply {
                binding.imageView1.setImageBitmap(this)
                println("applied")
            }
        }
    }

    private fun uploadProfileImageListener() {
            takeAnImageListener()
            browseAnImageListener()
    }

//    private fun openCamera() {
//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, "New Picture")
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
//
//        //camera intent
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//        // set filename
//        vFilename = "photo-"+ LocalDate.now().toString() + ".jpg"
//
//        // set direcory folder
//        val path = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_PICTURES
//        )
//        val file = File(path, vFilename);
//
//        val image_uri = FileProvider.getUriForFile(requireActivity(), requireActivity().getApplicationContext().getPackageName() + ".provider", file);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
//        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        //called when user presses ALLOW or DENY from Permission Request Popup
//        when(requestCode){
//            PERMISSION_CODE -> {
//                if (grantResults.size > 0 && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED){
//                    //permission from popup was granted
//                    openCamera()
//                } else{
//                    //permission from popup was denied
//                    Toast.makeText(activity,"Permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data);
//        println("codes>>>>>>>>>>>>>>>>>> " + resultCode + " " + AppCompatActivity.RESULT_OK)
//        if (resultCode== AppCompatActivity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
//            val path = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES
//            )
//            //File object of camera image
//            val file = File(path, vFilename);
////            Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show()
//
//
//            //Uri of camera image
//            val uri = FileProvider.getUriForFile(
//                requireActivity(),
//                requireActivity().getApplicationContext().getPackageName() + ".provider",
//                file
//            );
////            val myImageView = findViewById<ImageView>(R.id.imageView1)
//            binding.imageView1.setImageURI(uri)
//            println("uri///////////////////// " + uri.path)
//            pathImg = uri.path.toString()
////            user.profileImg = uri.path
////            userViewModel.updateUser()
//        }else if (resultCode== AppCompatActivity.RESULT_OK && requestCode == PICK_IMAGE) {
//            val imageUri = data?.data
////            val myImageView = findViewById<ImageView>(R.id.imageView1)
//            binding.imageView1.setImageURI(imageUri)
//            if (imageUri != null) {
//                pathImg = imageUri.path.toString()
//            }
////            if (imageUri != null) {
////                user.profileImg = imageUri.path
////                userViewModel.updateUser()
////            }
//        }
//    }
//
//    public fun setImg(): Boolean{
//        var result = false
//        if(pathImg!=""){
//            user.profileImg = pathImg
//            userViewModel.updateUser()
//            result = true
//        }
//        return result
//    }
}