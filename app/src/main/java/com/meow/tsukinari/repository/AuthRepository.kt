package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.meow.tsukinari.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val USERS_COLLECTION_REF = "users"

class AuthRepository {
    val currentUser: FirebaseUser? = Firebase.auth.currentUser

    private val usersRef = Firebase.database.getReference(USERS_COLLECTION_REF)

    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun getUserId(): String {
        return Firebase.auth.currentUser?.uid.orEmpty()
    }


    suspend fun checkUsername(userName: String): Boolean {
        return coroutineScope {
            val deferred = async {
                val query = usersRef.orderByChild("username").equalTo(userName)
                    .get()
                query.result.exists()
            }
            deferred.await()
        }
    }


    fun registerUser(
        userId: String, userName: String,
    ) {
        val user = UserModel(id = userId, username = userName) // add username property to user
        usersRef.child(userId).setValue(user) // use userId as key for child node
    }


    suspend fun signUp(email: String, password: String, isCompleted: (Boolean) -> Unit) =
        withContext(Dispatchers.IO) {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    isCompleted.invoke(true)
                } else {
                    isCompleted.invoke(false)
                }
            }.await()
        }

    suspend fun signIn(email: String, password: String, isCompleted: (Boolean) -> Unit) =
        withContext(Dispatchers.IO) {
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    isCompleted.invoke(true)
                } else {
                    isCompleted.invoke(false)
                }
            }.await()
        }

    suspend fun signOut() {
        Firebase.auth.signOut()
    }

    suspend fun resetPassword(email: String, isCompleted: (Boolean) -> Unit) =
        withContext(Dispatchers.IO) {
            Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isCompleted.invoke(true)
                } else {
                    isCompleted.invoke(false)
                }
            }.await()
        }

}