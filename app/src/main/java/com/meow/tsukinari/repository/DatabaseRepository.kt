package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.meow.tsukinari.model.FictionModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


const val USERS_COLLECTION_REF = "users"
const val FICTIONS_COLLECTION_REF = "fictions"
const val FOLLOWS_COLLECTION_REF = "follows"
const val CHAPTERS_COLLECTION_REF = "chapters"

class DatabaseRepository {

    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null
    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val usersRef = Firebase.database.getReference(USERS_COLLECTION_REF)
    private val fictionsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val chaptersRef = Firebase.database.getReference(CHAPTERS_COLLECTION_REF)
    private val followsRef = Firebase.database.getReference(FOLLOWS_COLLECTION_REF)

    fun getUserFictions(
        userId: String,
    ): Flow<Resources<List<FictionModel>>> = callbackFlow {

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    val fictions =
                        snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }

                    trySend(Resources.Success(data = fictions))
                } else {

                    trySend(Resources.Error(throwable = Exception("No data")))
                }
            }

            override fun onCancelled(error: DatabaseError) {

                trySend(Resources.Error(throwable = error.toException()))
            }
        }

        fictionsRef.orderByChild("uploaderId").equalTo(userId)
            .addValueEventListener(valueEventListener)

        awaitClose {
            fictionsRef.orderByChild("uploaderId").equalTo(userId)
                .removeEventListener(valueEventListener)
        }
    }

    fun getFiction(
        fictionId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (FictionModel?) -> Unit
    ) {

        fictionsRef.child(fictionId).equalTo(getUserId(), "uploaderId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    onSuccess.invoke(snapshot.getValue(FictionModel::class.java))
                }

                override fun onCancelled(error: DatabaseError) {

                    onError.invoke(error.toException())
                }
            })
    }

    fun addFiction(
        uploaderId: String,
        title: String,
        description: String,
        coverLink: String,
        onComplete: (Boolean) -> Unit,
    ) {

        val fictionId = fictionsRef.push().key ?: "null"

        val fiction = FictionModel(
            fictionId,
            uploaderId,
            title,
            description,
            coverLink,
        )

        fictionsRef.child(fictionId)
            .setValue(fiction)
            .addOnCompleteListener { result ->

                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteFiction(fictionId: String, onComplete: (Boolean) -> Unit) {

        fictionsRef.child(fictionId)
            .removeValue()
            .addOnCompleteListener { result ->

                onComplete.invoke(result.isSuccessful)
            }
    }

    fun updateFiction(
        title: String,
        description: String,
        fictionId: String,
        onResult: (Boolean) -> Unit
    ) {

        val updateData = hashMapOf<String, Any>(
            "description" to description,
            "title" to title,
            "fictionId" to fictionId
        )


        fictionsRef.child(fictionId)
            .updateChildren(updateData)
            .addOnCompleteListener { result ->

                onResult(result.isSuccessful)
            }
    }
    fun signOut() = Firebase.auth.signOut()
}


sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}


