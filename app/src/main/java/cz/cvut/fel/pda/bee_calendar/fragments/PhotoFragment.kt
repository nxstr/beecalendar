package cz.cvut.fel.pda.bee_calendar.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import cz.cvut.fel.pda.bee_calendar.activities.ProfileActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentPhotoBinding
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.utils.BitmapConverter
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoFragment: Fragment() {
    private lateinit var binding: FragmentPhotoBinding
    private lateinit var user: User
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
            }
        }
    }

    private fun uploadProfileImageListener() {
            takeAnImageListener()
            browseAnImageListener()
    }
}