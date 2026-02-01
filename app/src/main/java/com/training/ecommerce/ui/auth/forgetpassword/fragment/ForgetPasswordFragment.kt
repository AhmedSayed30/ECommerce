package com.training.ecommerce.ui.auth.forgetpassword.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.databinding.FragmentForgetPasswordBinding
import com.training.ecommerce.ui.auth.forgetpassword.viewmodel.ForgetPasswordViewModel
import com.training.ecommerce.ui.auth.forgetpassword.viewmodel.ForgetPasswordViewModelFactory
import com.training.ecommerce.ui.common.views.ProgressDialog
import com.training.ecommerce.ui.showSnakeBarError
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ForgetPasswordFragment : BottomSheetDialogFragment() {
    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity())}

    private var _binding: FragmentForgetPasswordBinding?= null
    private val binding get() = _binding!!

    private val viewModel: ForgetPasswordViewModel by viewModels {
        ForgetPasswordViewModelFactory()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.forgetPasswordStatus.collectLatest {
                it.let {
                    when(it){
                        is Resource.Loading -> {
                            progressDialog.show()
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showSentEmailSuccessDialog()
                        }
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            view?.showSnakeBarError(it.exception?.message.toString())
                        }
                    }
                }
            }
        }
    }
    private fun showSentEmailSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle("Reset Password").
                setMessage("We have sent you an email to reset your password. Please check your email.")
            .setPositiveButton(
                "ok"
            ){ dialog, which ->
                dialog?.dismiss()
                this@ForgetPasswordFragment.dismiss()
            }.create().show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    }

