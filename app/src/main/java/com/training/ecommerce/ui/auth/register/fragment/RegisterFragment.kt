package com.training.ecommerce.ui.auth.register.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.training.ecommerce.R
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.databinding.FragmentRegisterBinding
import com.training.ecommerce.ui.auth.login.fragment.LoginFragment
import com.training.ecommerce.ui.auth.register.viewmodel.RegisterViewModel
import com.training.ecommerce.ui.auth.register.viewmodel.RegisterViewModelFactory
import com.training.ecommerce.ui.common.views.LoadingDialog
import com.training.ecommerce.ui.home.MainActivity
import com.training.ecommerce.ui.showSnakeBarError
import com.training.ecommerce.utils.CrashlyticsUtils
import com.training.ecommerce.utils.LoginException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    val progressDialog by lazy { LoadingDialog(requireContext()) }

    private val viewModel: RegisterViewModel by viewModels{
        RegisterViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentRegisterBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect{
                it .let {
                    when (it) {
                        is Resource.Loading -> {
                            progressDialog.show()
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(),it.data?.id, Toast.LENGTH_LONG).show()
                            goToHome()
                        }

                        is Resource.Error -> {
                            progressDialog.dismiss()
                            Log.d(TAG,"errrrrror: ${it.exception?.message}")
                            view?.showSnakeBarError(it.exception?.message.toString() ?: getString(R.string.generic_err_msg))
                            logAuthIssueToCrashlytics(it.exception?.message.toString() ?: getString(R.string.generic_err_msg),"Register Error")

                        }
                    }
                }
            }
        }
    }

    private fun goToHome() {
        requireActivity().startActivity(Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.PROVIDER_KEY to provider
        )
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }

}