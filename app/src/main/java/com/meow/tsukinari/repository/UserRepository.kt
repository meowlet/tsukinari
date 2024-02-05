package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class UserRepository {
    val currentUser: FirebaseUser? = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null
    fun getUserInfo(): String = Firebase.auth.currentUser?.email.orEmpty()

}