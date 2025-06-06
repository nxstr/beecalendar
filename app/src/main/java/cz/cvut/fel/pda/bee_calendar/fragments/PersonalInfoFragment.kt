package cz.cvut.fel.pda.bee_calendar.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.cvut.fel.pda.bee_calendar.activities.ProfileActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentPersonalInfoBinding
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.utils.Vibrations
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
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
            Vibrations.vibrate(requireActivity())
            return false
        }
        if (binding.email.text.toString() == "") {
            Toast.makeText(activity, "Email is empty!", Toast.LENGTH_SHORT).show()
            Vibrations.vibrate(requireActivity())
            return false
        }

        if (binding.password.text.toString() != "" && binding.password.text.toString() != "password"){
            if (binding.password.text.toString() != binding.password2.text.toString()) {
                Toast.makeText(activity, "Password is not match!", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(requireActivity())
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
                    Vibrations.vibrate(requireActivity())
                    emailRepeat = false
                }
            }
        }
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
        return result
    }


}