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
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.model.FictionCommentModel
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.FictionStatsModel
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
const val COMMENTS_COLLECTION_REF = "comments"
const val STATS_COLLECTION_REF = "stats"

//Large data
const val IMAGES_COLLECTION_REF = "images"

class DatabaseRepository {

    val user = Firebase.auth.currentUser


    val userEmail = user?.email.orEmpty()

    private val fictionsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val chaptersRef = Firebase.database.getReference(CHAPTERS_COLLECTION_REF)
    private val followsRef = Firebase.database.getReference(FOLLOWS_COLLECTION_REF)
    private val fictionImagesRef = Firebase.storage.reference.child(IMAGES_COLLECTION_REF)
    private val commentsRef = Firebase.database.getReference(COMMENTS_COLLECTION_REF)
    private val usersRef = Firebase.database.getReference(USERS_COLLECTION_REF)
    private val statsRef = Firebase.database.getReference(STATS_COLLECTION_REF)

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

    //suspend get user info
    suspend fun getUserInfo(userId: String): Resources<UserModel> {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            val user = snapshot.getValue(UserModel::class.java)
            Resources.Success(data = user)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
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
        chapterId: String,
        onComplete: (String?) -> Unit
    ) {
        val filePath =
            fictionImagesRef.child(getUserId()).child(fictionId).child(chapterId)
                .child(pageNumber.toString())
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
        onSuccess: (FictionModel?) -> Unit,
    ) {
        fictionsRef.child(fictionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onSuccess(snapshot.getValue(FictionModel::class.java))

                    //get the like and dislike count
                    statsRef.child(fictionId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
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
        isFinished: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        when (imageUri) {
            Uri.EMPTY -> {
                // update data without image
                val updateData = hashMapOf<String, Any>(
                    "description" to description,
                    "title" to title,
                    "finished" to isFinished
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
                            "coverLink" to it,
                            "finished" to isFinished
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


    //update user's displayName
    fun updateDisplayName(displayName: String, onComplete: (Boolean) -> Unit) {
        val updateData = hashMapOf<String, Any>(
            "displayName" to displayName
        )
        usersRef.child(getUserId())
            .updateChildren(updateData)
            .addOnCompleteListener { result ->
                onComplete(result.isSuccessful)
            }
    }

    //update user's aboutMe
    fun updateAboutMe(aboutMe: String, onComplete: (Boolean) -> Unit) {
        val updateData = hashMapOf<String, Any>(
            "aboutMe" to aboutMe
        )
        usersRef.child(getUserId())
            .updateChildren(updateData)
            .addOnCompleteListener { result ->
                onComplete(result.isSuccessful)
            }
    }

    //update user's profile pic
    fun updateProfilePic(context: Context, imageUri: Uri, onComplete: (Boolean) -> Unit) {
        //compress the image
        val compressedImage = compressImage(listOf(imageUri), context)[0]
        val filePath = fictionImagesRef.child(getUserId()).child("profilePic")
        filePath.putFile(compressedImage)
            .addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener { imageUrl ->
                    val updateData = hashMapOf<String, Any>(
                        "profileImageUrl" to imageUrl.toString()
                    )
                    usersRef.child(getUserId())
                        .updateChildren(updateData)
                        .addOnCompleteListener { result ->
                            onComplete(result.isSuccessful)
                        }
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    //upload several images for a chapter, store the image url in the database, and return the image url the pass the image url to the chapter model, write the chapter model to the database
    fun addChapter(
        context: Context,
        fictionId: String,
        chapterNumber: Int,
        chapterTitle: String,
        imageUris: List<Uri>,
        onComplete: (Boolean) -> Unit
    ) {
        val chapterId = chaptersRef.push().key.orEmpty()
        val chapterPages = mutableListOf<String>()


        imageUris.forEachIndexed { index, uri ->
            uploadPage(uri, index, fictionId, chapterId) { imageUrl ->
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
            //
        }
    }

    //get chapters of a fiction
    fun getChapters(
        fictionId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (List<ChapterModel>?) -> Unit
    ) {
        chaptersRef.orderByChild("fictionId").equalTo(fictionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chapters =
                        snapshot.children.mapNotNull { it.getValue(ChapterModel::class.java) }
                    onSuccess(chapters)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    //get all images of a chapter
    fun getChapterImages(
        fictionId: String,
        chapterId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (List<String>?) -> Unit
    ) {
        chaptersRef.child(chapterId).child("chapterPages")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val images = snapshot.children.mapNotNull { it.value.toString() }
                    onSuccess(images)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }


    fun signOut() = Firebase.auth.signOut()
    fun getChapterData(chapterId: String, onSuccess: (ChapterModel) -> Unit) {
        //get the chapter from the database using the chapter id
        chaptersRef.child(chapterId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chapter = snapshot.getValue(ChapterModel::class.java)
                    onSuccess(chapter!!)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //comment on a fiction
    fun commentOnFiction(
        fictionId: String,
        comment: String,
        onComplete: (Boolean) -> Unit
    ) {
        val commentId = commentsRef.child(fictionId).push().key.orEmpty()
        val commentModel = FictionCommentModel(
            fictionId = fictionId,
            commentId = commentId,
            userId = getUserId(),
            comment = comment,
            commentTime = System.currentTimeMillis()
        )
        commentsRef.child(fictionId).child(commentId)
            .setValue(commentModel)
            .addOnCompleteListener { result ->
                onComplete(result.isSuccessful)
            }
    }

    //get all comment of a fiction order by time (greatest comment time first)
    fun getFictionComments(
        fictionId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (List<FictionCommentModel>?) -> Unit
    ) {
        commentsRef.child(fictionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments =
                        snapshot.children.mapNotNull { it.getValue(FictionCommentModel::class.java) }
                    onSuccess(comments)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }


    //comment on a chapter
    fun commentOnChapter(
        chapterId: String,
        comment: String,
        onComplete: (Boolean) -> Unit
    ) {
        val commentId = commentsRef.child(chapterId).child("comments").push().key.orEmpty()
        val commentModel = FictionCommentModel(
            commentId = commentId,
            userId = getUserId(),
            comment = comment,
            commentTime = System.currentTimeMillis()
        )
        commentsRef.child(chapterId).child("comments").child(commentId)
            .setValue(commentModel)
            .addOnCompleteListener { result ->
                onComplete(result.isSuccessful)
            }
    }

    //get all comment of a chapter
    fun getChapterComments(
        chapterId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (List<FictionCommentModel>?) -> Unit
    ) {
        commentsRef.child(chapterId).child("comments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments =
                        snapshot.children.mapNotNull { it.getValue(FictionCommentModel::class.java) }
                    onSuccess(comments)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }


    //get all available user's info
    fun getAllUserInfo(
        onError: (Throwable?) -> Unit,
        onSuccess: (List<UserModel>?) -> Unit
    ) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
                onSuccess(users)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }


    //get info on a user with no callback


    //like a fiction
    fun likeFiction(fictionId: String, onComplete: (Boolean, Int) -> Unit) {
        val likeRef = statsRef.child(fictionId)
        //update only the likedBy list, load the current likedBy list, add the user id to the list, and update the list

        val likedBy = mutableListOf<String>()
        //load the current likedBy list
        likeRef.child("likedBy").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get the user id who liked list
                val likedByList = snapshot.children.mapNotNull { it.value.toString() }
                likedBy.addAll(likedByList)
                //if the user id is not in the list, add it to the list
                if (!likedBy.contains(getUserId())) {
                    likedBy.add(getUserId())
                } else return
                likeRef.child("likedBy").setValue(likedBy)
                    .addOnCompleteListener { result ->
                        undislikeFiction(fictionId) { _, _ -> }
                        onComplete(result.isSuccessful, likedBy.size)
                        //undislke the fiction if the user has disliked the fiction
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //unlike a fiction
    fun unlikeFiction(fictionId: String, onComplete: (Boolean, Int) -> Unit) {
        val unlikeRef = statsRef.child(fictionId)
        //update only the likedBy list, load the current likedBy list, remove the user id from the list, and update the list

        val likedBy = mutableListOf<String>()
        //load the current likedBy list
        unlikeRef.child("likedBy").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get the user id who liked list
                val likedByList = snapshot.children.mapNotNull { it.value.toString() }
                likedBy.addAll(likedByList)
                //if the user id is in the list, remove it from the list
                if (likedBy.contains(getUserId())) {
                    likedBy.remove(getUserId())
                } else return
                unlikeRef.child("likedBy").setValue(likedBy)
                    .addOnCompleteListener { result ->
                        onComplete(result.isSuccessful, likedBy.size)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    //dislike a fiction
    fun dislikeFiction(fictionId: String, onComplete: (Boolean, Int) -> Unit) {
        val dislikeRef = statsRef.child(fictionId)
        //update only the dislikedBy list, load the current dislikedBy list, add the user id to the list, and update the list

        val dislikedBy = mutableListOf<String>()
        //load the current dislikedBy list
        dislikeRef.child("dislikedBy").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get the user id who disliked list
                val dislikedByList = snapshot.children.mapNotNull { it.value.toString() }
                dislikedBy.addAll(dislikedByList)
                //if the user id is not in the list, add it to the list
                if (!dislikedBy.contains(getUserId())) {
                    dislikedBy.add(getUserId())
                } else return
                dislikeRef.child("dislikedBy").setValue(dislikedBy)
                    .addOnCompleteListener { result ->
                        unlikeFiction(fictionId) { _, _ -> }
                        onComplete(result.isSuccessful, dislikedBy.size)
                        //unlike the fiction if the user has liked the fiction
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //undislike a fiction
    fun undislikeFiction(fictionId: String, onComplete: (Boolean, Int) -> Unit) {
        val undislikeRef = statsRef.child(fictionId)
        //update only the dislikedBy list, load the current dislikedBy list, remove the user id from the list, and update the list

        val dislikedBy = mutableListOf<String>()
        //load the current dislikedBy list
        undislikeRef.child("dislikedBy")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get the user id who disliked list
                    val dislikedByList = snapshot.children.mapNotNull { it.value.toString() }
                    dislikedBy.addAll(dislikedByList)
                    //if the user id is in the list, remove it from the list
                    if (dislikedBy.contains(getUserId())) {
                        dislikedBy.remove(getUserId())
                    } else return
                    undislikeRef.child("dislikedBy").setValue(dislikedBy)
                        .addOnCompleteListener { result ->
                            onComplete(result.isSuccessful, dislikedBy.size)
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    //get stats of a fiction
    fun getFictionStats(
        fictionId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (FictionStatsModel?) -> Unit
    ) {
        statsRef.child(fictionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stats = snapshot.getValue(FictionStatsModel::class.java)
                    onSuccess(stats)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    //add view to a chapter
    fun addViewToChapter(fictionId: String, chapterId: String, onComplete: (Boolean) -> Unit) {
        val viewRef = statsRef.child(fictionId).child("chapters").child(chapterId).child("viewedBy")
        val viewedBy = mutableListOf<String>()
        viewRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val viewedByList = snapshot.children.mapNotNull { it.value.toString() }
                viewedBy.addAll(viewedByList)
                if (getUserId().isNotBlank()) {
                    viewedBy.add(getUserId())
                } else {
                    viewedBy.add("anonymous")
                }
                viewRef.setValue(viewedBy)
                    .addOnCompleteListener { result ->
                        onComplete(result.isSuccessful)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //count the total views of a chapter
    fun countTotalViews(
        fictionId: String,
        chapterId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Int) -> Unit
    ) {
        statsRef.child(fictionId).child("chapters").child(chapterId).child("viewedBy")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val totalViews = snapshot.childrenCount.toInt()
                    onSuccess(totalViews)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    //get the total views of a fiction (sum all the views of all the chapters of the fiction)
    fun getTotalViews(
        fictionId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Int) -> Unit
    ) {
        statsRef.child(fictionId).child("chapters")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalViews = 0
                    snapshot.children.forEach { chapter ->
                        totalViews += chapter.child("viewedBy").childrenCount.toInt()
                    }
                    onSuccess(totalViews)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    suspend fun getTotalViews(fictionId: String): Int {
        return try {
            val snapshot = statsRef.child(fictionId).child("chapters").get().await()
            var totalViews = 0
            snapshot.children.forEach { chapter ->
                totalViews += chapter.child("viewedBy").childrenCount.toInt()
            }
            totalViews
        } catch (e: Exception) {
            0
        }
    }

    //suspend function to get total likes of a fiction
    suspend fun getTotalLikes(fictionId: String): Int {
        return try {
            val snapshot = statsRef.child(fictionId).child("likedBy").get().await()
            snapshot.childrenCount.toInt()
        } catch (e: Exception) {
            0
        }
    }

}


sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}


