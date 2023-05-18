package cz.cvut.fel.pda.bee_calendar.fragments

import android.R
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import cz.cvut.fel.pda.bee_calendar.activities.ProfileActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentPersonalInfoBinding
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

class PersonalInfoFragment: Fragment() {
    private lateinit var binding: FragmentPersonalInfoBinding
    private lateinit var user: User
    private lateinit var sp: SharedPreferences

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        (activity as ProfileActivity).supportActionBar?.title = "PERSONAL INFO"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val toolbar = requireActivity().findViewById<>(cz.cvut.fel.pda.bee_calendar.R.id.toolbar)

//        val menu = requireActivity().actionBar
//        menu.setOnMenuItemClickListener {
//            if(validate() && validateEmail()) {
//                setResult()
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(cz.cvut.fel.pda.bee_calendar.R.id.profile_fr, ProfileSettingsFragment())
//                transaction.commit()
//            }else{
//                println("aaaaaaaaaaaaaa")
//            }
//            true
//        }
//        val menuHost: MenuHost = requireActivity()
////
//        menuHost.addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                // Add menu items here
////                menuInflater.inflate(cz.cvut.fel.pda.bee_calendar.R.menu.action_menu, menu)
//                menu.findItem(cz.cvut.fel.pda.bee_calendar.R.id.navigation_save).setVisible(true)
//            }
////
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
////                // Handle the menu selection
////                if(menuItem.itemId==cz.cvut.fel.pda.bee_calendar.R.id.navigation_save){
////                    if(validate() && validateEmail()) {
////                        setResult()
////                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
////                        transaction.replace(cz.cvut.fel.pda.bee_calendar.R.id.profile_fr, ProfileSettingsFragment())
////                        transaction.commit()
////                    }else{
////                        Toast.makeText(activity, "Can not be saved", Toast.LENGTH_SHORT).show()
////                        println("aaaaaaaaaaaaaa------------")
////                    }
////                }
//
//                return true
//            }
//        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        user = userViewModel.loggedUser!!
        setData()

    }

    private fun setData(){
        binding.firstname.setText(user.firstName)
        binding.lastname.setText(user.lastName)
        binding.email.setText(user.email)

    }

    private fun validate(): Boolean {

        if (binding.firstname.text.toString() == "" || binding.lastname.text.toString() == "") {
            Toast.makeText(activity, "Name is empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.email.text.toString() == "") {
            Toast.makeText(activity, "Email is empty!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.password.text.toString() != "" && binding.password.text.toString() != "password"){
            if (binding.password.text.toString() != binding.password2.text.toString()) {
                Toast.makeText(activity, "Password is not match!", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun validateEmail(): Boolean{
        var emailRepeat = true
        runBlocking {
            if(binding.email.text.toString()!=user.email && binding.email.text.toString()!=""){
                val testUser = userViewModel.getByEmail(binding.email.text.toString())
                if(testUser!=null){
                    Toast.makeText(activity, "This email is already exist!", Toast.LENGTH_SHORT).show()
                    emailRepeat = false
                }
            }
        }
        println("email ================ " + emailRepeat)
        return emailRepeat
    }

    @SuppressLint("SuspiciousIndentation")
    fun setResult():Boolean{

        var result = false
        if(validate() && validateEmail()) {
            user.firstName = binding.firstname.text.toString()
            user.lastName = binding.lastname.text.toString()
            user.email = binding.email.text.toString()
            if (binding.password.text.toString() != "" && binding.password.text.toString() != "password"){
                user.password = binding.password.text.toString()
            }
            userViewModel.updateUser()
            result = true
        }
        println("here-----------------res" + result)
        return result
    }


}