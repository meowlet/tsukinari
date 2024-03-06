package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.meow.tsukinari.model.UserModel
import kotlinx.coroutines.Dispatchers
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

    fun getUserEmail(): String {
        return Firebase.auth.currentUser?.email.orEmpty()
    }


    fun insertUsername(username: String, callback: (success: Boolean) -> Unit) {

        usersRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Username đã tồn tại
                if (dataSnapshot.exists()) {
                    callback(false)
                    return
                }
                callback(true)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false)
            }
        })
    }

    // Check username availability
    suspend fun checkUsername(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            usersRef.child(username).get().await().exists()
        }
    }

    // Register user
    fun registerUser(
        userId: String,
        email: String,
        userName: String,
        createdAt: Long,
        isCompleted: (Boolean) -> Unit
    ) {
        val user = UserModel(
            id = userId,
            email = email,
            username = userName,
            createdAt = createdAt
        ) // add username property to user
        usersRef.child(userName).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                isCompleted.invoke(true)
            } else {
                isCompleted.invoke(false)
            }
        } // use userId as key for child node
    }


    suspend fun signUp(
        email: String,
        password: String,
        isCompleted: (Boolean) -> Unit
    ) =
        withContext(Dispatchers.IO) {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
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

    fun signOut() {
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