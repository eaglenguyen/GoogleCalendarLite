package com.example.calandermvvm.domain


import com.example.calandermvvm.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepoAuthImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
): RepoAuth {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser


    override suspend fun loginUser(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email,password).await()
            Resource.Success(result.user!!)
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.toString())
        }
    }

    override suspend fun registerUser(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            Resource.Success(result.user!!)
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.toString())
        }
    }


}