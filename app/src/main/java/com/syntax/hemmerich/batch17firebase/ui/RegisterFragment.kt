package com.syntax.hemmerich.batch17firebase.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.syntax.hemmerich.batch17firebase.R
import com.syntax.hemmerich.batch17firebase.databinding.FragmentRegisterBinding


class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btRegister.setOnClickListener {
            val email: String = binding.tietEmail.text.toString()
            val pass: String = binding.tietPass.text.toString()

            if (email != "" && pass != "") {
                mainViewModel.register(email, pass)
            }

        }

        mainViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(R.id.homeFragment)
            }
        }

    }
}