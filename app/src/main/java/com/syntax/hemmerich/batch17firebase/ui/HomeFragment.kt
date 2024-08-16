package com.syntax.hemmerich.batch17firebase.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.syntax.hemmerich.batch17firebase.R
import com.syntax.hemmerich.batch17firebase.databinding.FragmentHomeBinding
import com.syntax.hemmerich.batch17firebase.databinding.FragmentLoginBinding


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null){
            mainViewModel.uploadImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.tvEmail.text = mainViewModel.currentUser.value?.email ?: ""

        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
            findNavController().navigate(R.id.loginFragment)
        }
        binding.btnUploadImage.setOnClickListener {
            getContent.launch("images/*")
        }

    }


}