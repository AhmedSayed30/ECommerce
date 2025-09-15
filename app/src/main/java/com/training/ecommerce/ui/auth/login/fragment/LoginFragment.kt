package com.training.ecommerce.ui.auth.login.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.training.ecommerce.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.training.ecommerce.R
import com.training.ecommerce.data.datasourse.datastore.UserPreferencesDataSource
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.training.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.training.ecommerce.databinding.FragmentLoginBinding
import com.training.ecommerce.ui.auth.login.viewmodel.LoginViewModel
import com.training.ecommerce.ui.auth.login.viewmodel.LoginViewModelFactory
import com.training.ecommerce.ui.common.views.ProgressDialog
import com.training.ecommerce.ui.home.MainActivity
import com.training.ecommerce.ui.showRetrySnakeBarError
import com.training.ecommerce.ui.showSnakeBarError
import com.training.ecommerce.utils.CrashlyticsUtils
import com.training.ecommerce.utils.LoginException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }
    val progressDialog by lazy {ProgressDialog.createProgressDialog(requireActivity())}
    private var _binding: FragmentLoginBinding? = null
    private val binging get() = _binding!!
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPref = UserPreferencesRepositoryImpl(
                UserPreferencesDataSource(
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
        initViewModel()
        initListeners()

    }

    private fun initListeners() {
        binging.btnGoogleLogIn.setOnClickListener {
            loginWithGoogleRequest()
        }
        binging.btnFacebookLogIn.setOnClickListener {
            if(isLoggedIn()){
                loginManager.logOut()
            }else {
                loginWithFacebookRequest()
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    private fun loginWithFacebookRequest() {
        loginManager.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException) {
                val msg = error.message ?: getString(R.string.generic_err_msg)
                Log.d(TAG, "onError: $msg")
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg,"Facebook")
            }

            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                Log.d(TAG, "onSuccess: $token")
                viewModel.loginWithFacebook(token)
            }
        })

        loginManager.logInWithReadPermissions(
            this,
            callbackManager,
            listOf("email","public_profile")
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.d(TAG, "initListeners: ${it.resultCode}")
        if (it.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleSignInResult(task)
        } else{
            view?.showSnakeBarError(getString(R.string.google_sign_in_failed_msg))
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.PROVIDER_KEY to provider
        )
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.loginWithGoogle(account.idToken!!)
        }catch (e: Exception){
            view?.showRetrySnakeBarError(e.message ?: getString(R.string.generic_err_msg)){
                loginWithGoogleRequest()
            }
            logAuthIssueToCrashlytics(e.message ?: getString(R.string.generic_err_msg),"Google")
        }
    }

    private fun loginWithGoogleRequest() {
        val gso =GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.clientServerId)
            .requestEmail()
            .requestProfile()
            .requestServerAuthCode(BuildConfig.clientServerId)
            .build()
        Log.d(TAG, "loginWithGoogleRequest: ${BuildConfig.clientServerId}")
        val googleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)
        googleSignInClient.signOut()

        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collectLatest {
                it .let {
                    when (it) {
                        is Resource.Loading -> {
                            progressDialog.show()
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(),it.data,Toast.LENGTH_LONG).show()
                            goToHome()
                        }

                        is Resource.Error -> {
                            progressDialog.dismiss()
                            view?.showSnakeBarError(it.exception?.message.toString() ?: getString(R.string.generic_err_msg))
                            logAuthIssueToCrashlytics(it.exception?.message.toString() ?: getString(R.string.generic_err_msg),"Login Error")

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

    companion object {
       private const val TAG = "LoginFragment"
    }
}
