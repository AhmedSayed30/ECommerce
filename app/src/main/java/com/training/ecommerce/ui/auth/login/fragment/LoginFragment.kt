package com.training.ecommerce.ui.auth.login.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.training.ecommerce.R
import com.training.ecommerce.data.datasourse.datastore.UserPreferencesDataSourse
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.training.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.training.ecommerce.databinding.FragmentLoginBinding
import com.training.ecommerce.ui.auth.login.viewmodel.LoginViewModel
import com.training.ecommerce.ui.auth.login.viewmodel.LoginViewModelFactory


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binging get() = _binding!!
    val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPref = UserPreferencesRepositoryImpl(
                UserPreferencesDataSourse(
                    requireActivity())),
            authRepository = FirebaseAuthRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        binging.lifecycleOwner = viewLifecycleOwner
        binging.vm = viewModel
        return binging.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
       private const val TAG = "LoginFragment"
    }
}