package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database


const val USERS_COLLECTION_REF = "users"
const val FICTIONS_COLLECTION_REF = "users"
const val FOLLOWS_COLLECTION_REF = "users"
const val CHAPTERS_COLLECTION_REF = "users"

class DatabaseRepository {

    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null
    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val usersRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val fictionsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val chaptersRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val followsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)


}