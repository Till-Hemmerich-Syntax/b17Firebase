package com.syntax.hemmerich.batch17firebase.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.syntax.hemmerich.batch17firebase.R
import com.syntax.hemmerich.batch17firebase.databinding.FragmentHomeBinding
import com.syntax.hemmerich.batch17firebase.databinding.FragmentLoginBinding
import com.syntax.hemmerich.batch17firebase.model.Profile


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var analytics : FirebaseAnalytics
    //die Funktion definiert das wir eine Anfrage an das System nach Resourcen stellen(hier Fotos)
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null){
            //nutzt die uploadImage function aus dem Viewmodel um das Bild hochzuladen in FirebaseStorage
            mainViewModel.uploadImage(it)
        }
    }
    private var profileLink : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btLogout.setOnClickListener {
            mainViewModel.logout()
            findNavController().navigate(R.id.loginFragment)
        }
        binding.btUpdateImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btUpdate.setOnClickListener {
            val firstName = binding.tietFirstName.text.toString()
            val lastName = binding.tietLastName.text.toString()
            val phoneNumber = binding.tietPhoneNumber.text.toString()
            //führt ein "Custom" Event aus.
            analytics.logEvent("ProfileUpdate"){
                param("firstname",firstName)
                param("TEST","HALLO BAtch17")
            }
            //die UpdateProfile Funktion updated das Profile mit denn neuen daten im Firestore über unser Viewmodel
            mainViewModel.updateProfile(Profile(firstName,lastName,phoneNumber,profileLink))
        }
        // wir beobachten hier die Momentanaufnahme aus unserer DB, sollte sich diese ändern bekommen wir ein neues Object
        mainViewModel.profileRef.addSnapshotListener { snapShot, error ->
            if(error == null && snapShot != null){
                val updateProfile = snapShot.toObject(Profile::class.java)
                binding.tietFirstName.setText(updateProfile?.firstName)
                binding.tietLastName.setText(updateProfile?.lastName)
                binding.tietPhoneNumber.setText(updateProfile?.phoneNumber)
                if(updateProfile?.profilePicture != ""){
                    profileLink = updateProfile?.profilePicture!!
                    binding.ivProfilePicture.load(profileLink)

                }
            }
        }

    }

}