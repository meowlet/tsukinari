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
            usersRef.orderByChild("username").equalTo(username).get().await().value != null
        }
    }

    // Register user
    fun registerUser(
        userName: String,
        email: String,
        createdAt: Long,
        isCompleted: (Boolean) -> Unit
    ) {
        val user = UserModel(
            userId = getUserId(),
            userName = userName,
            email = email,
            createdAt = createdAt
        ) // add username property to user
        usersRef.child(getUserId()).setValue(user).addOnCompleteListener {
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

    //delete user
    fun deleteUser() {
        Firebase.auth.currentUser?.delete()
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