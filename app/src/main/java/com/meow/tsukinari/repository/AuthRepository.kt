package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    val currentUser: FirebaseUser? = Firebase.auth.currentUser

    fun HasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun GetUserId(): String {
        return Firebase.auth.currentUser?.uid.orEmpty()
    }


    suspend fun SignUp(email: String, password: String, isCompleted: (Boolean) -> Unit) =
        withContext(Dispatchers.IO) {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    isCompleted.invoke(true)
                } else {
                    isCompleted.invoke(false)
                }
            }.await()
        }

    suspend fun SignIn(email: String, password: String, isCompleted: (Boolean) -> Unit) =
        withContext(Dispatchers.IO) {
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    isCompleted.invoke(true)
                } else {
                    isCompleted.invoke(false)
                }
            }.await()
        }

    suspend fun SignOut() {
        Firebase.auth.signOut()
    }

    fun ResetPassword(email: String, isCompleted: (Boolean) -> Unit) {
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isCompleted.invoke(true)
            } else {
                isCompleted.invoke(false)
            }
        }
    }

}