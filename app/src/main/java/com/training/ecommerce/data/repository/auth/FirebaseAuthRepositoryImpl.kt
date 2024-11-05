package com.training.ecommerce.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.training.ecommerce.data.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(private val auth: FirebaseAuth = FirebaseAuth.getInstance())
    :FirebaseAuthRepository{
    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val authResult = auth.signInWithEmailAndPassword(email,password).await()
            authResult.user?.let {
                emit(Resource.Success(it.uid))
            } ?:run{
                emit(Resource.Error(Exception("User not found")))
            }
        } catch (e:Exception){
            emit(Resource.Error(e))
        }
    }
}