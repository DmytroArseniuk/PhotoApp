package com.arsars.photoapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.arsars.photoapp.R
import com.arsars.photoapp.ServiceLocator
import com.arsars.photoapp.base.SimpleViewModelFactory
import com.arsars.photoapp.databinding.FragmentLoginBinding
import com.arsars.photoapp.login.usecases.LoginUseCase
import com.arsars.photoapp.login.usecases.RegisterUseCase
import com.arsars.photoapp.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels(factoryProducer = {
        SimpleViewModelFactory {
            val dispatcher = Dispatchers.IO
            LoginViewModel(
                AuthInteractor(
                    ServiceLocator.userPreferences,
                    LoginUseCase(
                        ServiceLocator.userPreferences,
                        ServiceLocator.cryptoManager,
                        dispatcher
                    ),
                    RegisterUseCase(
                        ServiceLocator.userPreferences,
                        ServiceLocator.cryptoManager,
                        dispatcher
                    )
                )
            )
        }
    })
    var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                binding?.apply {
                    if (it.loading) {
                        login.visibility = View.INVISIBLE
                        progress.visibility = View.VISIBLE
                    } else {
                        login.visibility = View.VISIBLE
                        progress.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.event.collect {
                when (it) {
                    is LoginViewModel.Event.Error -> binding?.apply {
                        Snackbar.make(root, R.string.incorrect_password, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    LoginViewModel.Event.SuccessfulLogin -> binding?.apply {
                        Navigation.findNavController(root)
                            .navigate(LoginFragmentDirections.actionLoginFragmentToPhotosFragment())
                    }
                }

            }
        }
        binding?.apply {
            login.setOnClickListener {
                activity.hideKeyboard()
                viewModel.login(password.text.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}