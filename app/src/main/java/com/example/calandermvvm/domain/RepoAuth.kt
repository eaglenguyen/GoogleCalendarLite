package com.example.calandermvvm.domain

import com.example.calandermvvm.util.Resource
import com.google.firebase.auth.FirebaseUser


interface RepoAuth {
    val currentUser: FirebaseUser?
    suspend fun loginUser(email: String, password: String): Resource<FirebaseUser>
    suspend fun registerUser(email: String,password: String): Resource<FirebaseUser>
}