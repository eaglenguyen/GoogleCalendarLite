package com.example.calandermvvm.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.calandermvvm.databinding.FragmentLoginBinding
import com.example.calandermvvm.util.Constant.ERROR_MESSAGE
import com.example.calandermvvm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private var email: String = ""
    private var password: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            binding.loginButton.setOnClickListener {
                email = binding.emailFieldLog.text.toString()
                password = binding.passwordFieldLog.text.toString()

                if (email.isEmpty()) {
                    binding.emailFieldLog.error = ERROR_MESSAGE
                }
                if (password.isEmpty()) {
                    binding.passwordFieldLog.error = ERROR_MESSAGE
                }

                    viewModel.onLoginResult(email, password)

                }

            binding.createButton.setOnClickListener {
                val email = binding.emailFieldLog.text.toString()
                val password = binding.passwordFieldLog.text.toString()

                if (email.isEmpty()) {
                    binding.emailFieldLog.error = ERROR_MESSAGE
            }
                if (password.isEmpty()) {
                    binding.passwordFieldLog.error = ERROR_MESSAGE
            }

                    viewModel.createUser(email, password)

        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect{
                    val id = it?.data?.uid.toString()
                    val userEmail = it?.data?.email.toString()
                    val action = LoginFragmentDirections.actionLoginFragmentToCalendarFragment(id,userEmail)
                    when(it) {
                        is Resource.Error -> Toast.makeText(this@LoginFragment.requireActivity(),"Error", Toast.LENGTH_SHORT).show()
                        is Resource.Loading -> Toast.makeText(this@LoginFragment.requireActivity(),"Loading", Toast.LENGTH_SHORT).show()
                        is Resource.Success -> {
                            Toast.makeText(requireActivity().applicationContext, "Success", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(action)
                        }
                        else -> Unit
                    }
                }
            }
        }






    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}