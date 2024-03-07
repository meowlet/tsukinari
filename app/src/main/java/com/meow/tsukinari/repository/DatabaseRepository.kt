package com.meow.tsukinari.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.UserModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

//Small data

const val FICTIONS_COLLECTION_REF = "fictions"
const val FOLLOWS_COLLECTION_REF = "follows"
const val CHAPTERS_COLLECTION_REF = "chapters"

//Large data
const val IMAGES_COLLECTION_REF = "images"

class DatabaseRepository {

    val user = Firebase.auth.currentUser


    val userEmail = user?.email.orEmpty()

    private val fictionsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val chaptersRef = Firebase.database.getReference(CHAPTERS_COLLECTION_REF)
    private val followsRef = Firebase.database.getReference(FOLLOWS_COLLECTION_REF)
    private val fictionImagesRef = Firebase.storage.reference.child(IMAGES_COLLECTION_REF)
    private val usersRef = Firebase.database.getReference("users")

    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun getUserId(): String {
        return Firebase.auth.currentUser?.uid.orEmpty()
    }

    //get user info from database by user id (search for the node that has the id value (not the key) is equal to the user id) return UserModel
    fun getUserInfo(
        userId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (UserModel?) -> Unit
    ) {
        //get all the info in the uid child node
        usersRef.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onSuccess(snapshot.getValue(UserModel::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }


    //search fictions
    suspend fun searchFictions(searchValue: String): Resources<List<FictionModel>> {
        return try {
            val snapshot =
                fictionsRef.orderByChild("title").startAt(searchValue).endAt(searchValue + "\uf8ff")
                    .get().await()
            val fictions = snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
            Resources.Success(data = fictions)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }


    fun uploadCover(imageUri: Uri, fictionId: String, onComplete: (String?) -> Unit) {
        val filePath = fictionImagesRef.child(getUserId()).child(fictionId).child("cover")
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

    fun uploadPage(
        imageUri: Uri,
        pageNumber: Int,
        fictionId: String,
        onComplete: (String?) -> Unit
    ) {
        val filePath =
            fictionImagesRef.child(getUserId()).child(fictionId).child(pageNumber.toString())
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


    //rewrite thif getfictions function to only load fictions and return it, no need to listen after the data is loaded
    suspend fun getFictions(): Resources<List<FictionModel>> {
        return try {
            val snapshot = fictionsRef.get().await()
            val fictions = snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
            Resources.Success(data = fictions)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }

//    fun getFictions(): Flow<Resources<List<FictionModel>>> = callbackFlow {
//        val valueEventListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val fictions =
//                    snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
//                trySend(Resources.Success(data = fictions))
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                trySend(Resources.Error(throwable = error.toException()))
//            }
//        }
//
//        fictionsRef.addValueEventListener(valueEventListener)
//
//        awaitClose {
//            fictionsRef.removeEventListener(valueEventListener)
//        }
//    }

    fun getUserFictions(userId: String): Flow<Resources<List<FictionModel>>> = callbackFlow {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fictions =
                    snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
                trySend(Resources.Success(data = fictions))
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


    fun compressImage(uncompressedImages: List<Uri>, context: Context): List<Uri> {
        //compress the image and return the compressed image uri
        val compressedImages = mutableListOf<Uri>()
        uncompressedImages.forEach { uri ->
            val tempFile = File.createTempFile("temp", "jpg")

            Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(RequestOptions().override(1080))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        resource.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            FileOutputStream(tempFile)
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            compressedImages.add(Uri.fromFile(tempFile))
        }
        return compressedImages
    }


    //get fictions's uploader's full info by passing the uploader id
    fun getUploaderInfo(
        uploaderId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (UserModel?) -> Unit
    ) {
        usersRef.child(uploaderId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onSuccess(snapshot.getValue(UserModel::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }


    fun getFiction(
        fictionId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (FictionModel?) -> Unit
    ) {
        fictionsRef.child(fictionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onSuccess(snapshot.getValue(FictionModel::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
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
        val fictionId = fictionsRef.push().key.orEmpty()

        uploadCover(imageUri, fictionId) { imageUrl ->
            imageUrl?.let {
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
            } ?: onComplete(false)
        }
    }

    fun deleteFiction(fictionId: String, onComplete: (Boolean) -> Unit) {
        fictionId.let {
            fictionsRef.child(it)
                .removeValue()
                .addOnCompleteListener { result ->
                    fictionImagesRef.child(userEmail).child(it).delete()
                    onComplete(result.isSuccessful)
                }
        }
    }

    fun updateFiction(
        title: String,
        description: String,
        fictionId: String,
        imageUri: Uri,
        onResult: (Boolean) -> Unit
    ) {
        when (imageUri) {
            Uri.EMPTY -> {
                // update data without image
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

            else -> {
                // upload image and update data with image
                uploadCover(imageUri, fictionId) { imageUrl ->
                    imageUrl?.let {
                        val updateData = hashMapOf<String, Any>(
                            "description" to description,
                            "title" to title,
                            "coverLink" to it
                        )
                        fictionsRef.child(fictionId)
                            .updateChildren(updateData)
                            .addOnCompleteListener { result ->
                                onResult(result.isSuccessful)
                            }
                    }
                }
            }
        }
    }

    //upload several images for a chapter, store the image url in the database, and return the image url the pass the image url to the chapter model, write the chapter model to the database
    suspend fun addChapter(
        context: Context,
        fictionId: String,
        chapterNumber: Int,
        chapterTitle: String,
        imageUris: List<Uri>,
        onComplete: (Boolean) -> Unit
    ) {
        val chapterId = chaptersRef.push().key.orEmpty()
        val chapterPages = mutableListOf<String>()

        val compressedImages = compressImage(imageUris, context)

        compressedImages.forEachIndexed { index, uri ->
            uploadPage(uri, index, fictionId) { imageUrl ->
                imageUrl?.let {
                    chapterPages.add(it)
                    if (index == imageUris.size - 1) {
                        val chapter = hashMapOf(
                            "chapterId" to chapterId,
                            "fictionId" to fictionId,
                            "chapterNumber" to chapterNumber,
                            "chapterTitle" to chapterTitle,
                            "chapterPages" to chapterPages,
                            "uploadedAt" to System.currentTimeMillis()
                        )

                        chaptersRef.child(chapterId).setValue(chapter)
                            .addOnCompleteListener { result ->
                                onComplete(result.isSuccessful)
                            }
                    }
                }
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


