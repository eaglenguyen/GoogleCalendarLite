package com.example.calandermvvm.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calandermvvm.domain.RepoAuth
import com.example.calandermvvm.util.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: RepoAuth
): ViewModel() {

    private val _state = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val state: StateFlow<Resource<FirebaseUser>?> = _state

    private val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        if (repository.currentUser != null) {
            _state.value = Resource.Success(currentUser!!)
        }
    }

    fun onLoginResult (email: String, password: String) = viewModelScope.launch {
        _state.value = Resource.Loading()
        val result = repository.loginUser(email, password)
        _state.value = result
    }

    fun createUser (email: String, password: String) = viewModelScope.launch {
        _state.value = Resource.Loading()
        val result = repository.registerUser(email, password)
        _state.value = result
    }


}