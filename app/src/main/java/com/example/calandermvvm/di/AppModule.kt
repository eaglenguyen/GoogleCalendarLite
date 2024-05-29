package com.example.calandermvvm.di

import com.example.calandermvvm.domain.RepoAuth
import com.example.calandermvvm.domain.RepoAuthImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()


    @Provides
    fun provideDatabase() = FirebaseDatabase.getInstance().reference.child("Tasks").child(
        provideFirebaseAuth().currentUser?.uid.toString()
    )

    @Provides
    fun providesAuthRepository(impl: RepoAuthImpl): RepoAuth = impl
}