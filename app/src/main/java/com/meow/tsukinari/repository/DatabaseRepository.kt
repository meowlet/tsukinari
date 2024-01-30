package com.meow.tsukinari.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import com.meow.tsukinari.model.FictionModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

//Small data
const val USERS_COLLECTION_REF = "users"
const val FICTIONS_COLLECTION_REF = "fictions"
const val FOLLOWS_COLLECTION_REF = "follows"
const val CHAPTERS_COLLECTION_REF = "chapters"

//Large data
const val IMAGES_COLLECTION_REF = "images"

class DatabaseRepository {

    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null
    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val usersRef = Firebase.database.getReference(USERS_COLLECTION_REF)
    private val fictionsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val chaptersRef = Firebase.database.getReference(CHAPTERS_COLLECTION_REF)
    private val followsRef = Firebase.database.getReference(FOLLOWS_COLLECTION_REF)

    private val fictionImagesRef = Firebase.storage.reference.child(IMAGES_COLLECTION_REF)


    fun uploadImage(imageUri: Uri, fictionId: String, onComplete: (String?) -> Unit) {
        val filePath = fictionImagesRef.child(getUserId()).child("$fictionId")
        filePath.putFile(imageUri)
            .addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener { imageUrl ->
                    onComplete(imageUrl.toString())
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


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

        fictionsRef.child(fictionId)
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
        imageUri: Uri,
        onComplete: (Boolean) -> Unit,
    ) {

        val fictionId = fictionsRef.push().key ?: "null"

        uploadImage(imageUri, fictionId) { imageUrl ->
            if (imageUrl != null) {
                val fiction = FictionModel(
                    uploaderId = uploaderId,
                    title = title,
                    description = description,
                    fictionId = fictionId,
                    coverLink = imageUrl,
                )

                // Add fiction to database
                fictionsRef.child(fictionId).setValue(fiction)
                    .addOnCompleteListener { result ->
                        onComplete(result.isSuccessful)
                    }
            } else {
                onComplete(false)
            }
        }
    }

    fun deleteFiction(fictionId: String, onComplete: (Boolean) -> Unit) {

        fictionsRef.child(fictionId)
            .removeValue()
            .addOnCompleteListener { result ->
                fictionImagesRef.child(getUserId()).child(fictionId).delete()
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun updateFiction(
        title: String,
        description: String,
        fictionId: String,
        imageUri: Uri,
        onResult: (Boolean) -> Unit
    ) {

        if (imageUri != Uri.EMPTY) {
            uploadImage(imageUri, fictionId) { imageUrl ->
                if (imageUrl != null) {
                    val updateData = hashMapOf<String, Any>(
                        "description" to description,
                        "title" to title,
                        "coverLink" to imageUrl
                    )
                    fictionsRef.child(fictionId)
                        .updateChildren(updateData)
                        .addOnCompleteListener { result ->
                            onResult(result.isSuccessful)
                        }
                }
            }
        } else {
            val updateData = hashMapOf<String, Any>(
                "description" to description,
                "title" to title,
            )
            fictionsRef.child(fictionId)
                .updateChildren(updateData)
                .addOnCompleteListener { result ->
                    onResult(result.isSuccessful)
                }
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


