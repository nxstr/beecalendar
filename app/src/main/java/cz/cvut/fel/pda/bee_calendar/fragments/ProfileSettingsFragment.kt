package cz.cvut.fel.pda.bee_calendar.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import cz.cvut.fel.pda.bee_calendar.activities.NotLoggedInActivity
import cz.cvut.fel.pda.bee_calendar.activities.ProfileActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentProfileSettingsBinding
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.utils.BitmapConverter
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel


class ProfileSettingsFragment: Fragment() {
    private lateinit var binding: FragmentProfileSettingsBinding
    private lateinit var user: User
    private lateinit var sp: SharedPreferences

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        (activity as ProfileActivity).supportActionBar?.title = "PROFILE"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
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

        sp = requireActivity().getSharedPreferences("logged-in-user", AppCompatActivity.MODE_PRIVATE)
        user = userViewModel.loggedUser!!
        binding.name.setText(user.firstName + " " + user.lastName)
        binding.email.setText(user.email)
        if(user.profileImg!="" && user.profileImg!=null) {
            user.profileImg.apply {
                binding.imageView1.setImageBitmap(this?.let { BitmapConverter.convert(it) })
            }
        }

        binding.personInfo.setOnClickListener {
            Toast.makeText(activity, "Change person info", Toast.LENGTH_SHORT).show()
            val transaction = requireActivity().supportFragmentManager.beginTransaction().addToBackStack("info")
            transaction.replace(cz.cvut.fel.pda.bee_calendar.R.id.profile_fr, PersonalInfoFragment())
            transaction.commit()
        }

        binding.photo.setOnClickListener {
            Toast.makeText(activity, "Change photo", Toast.LENGTH_SHORT).show()
            val transaction = requireActivity().supportFragmentManager.beginTransaction().addToBackStack("photo")
            transaction.replace(cz.cvut.fel.pda.bee_calendar.R.id.profile_fr, PhotoFragment())
            transaction.commit()
        }
        binding.logout.setOnClickListener {
            sp.apply{
                val spEditor = edit()
                spEditor.remove("user-id")
                spEditor.apply()

            }
            val intent = Intent(activity, NotLoggedInActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }
}