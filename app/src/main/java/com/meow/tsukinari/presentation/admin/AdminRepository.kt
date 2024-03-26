package com.meow.tsukinari.presentation.admin

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import com.meow.tsukinari.model.AllStatsModel
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.repository.CHAPTERS_COLLECTION_REF
import com.meow.tsukinari.repository.COMMENTS_COLLECTION_REF
import com.meow.tsukinari.repository.FICTIONS_COLLECTION_REF
import com.meow.tsukinari.repository.FOLLOWS_COLLECTION_REF
import com.meow.tsukinari.repository.IMAGES_COLLECTION_REF
import com.meow.tsukinari.repository.Resources
import com.meow.tsukinari.repository.STATS_COLLECTION_REF
import com.meow.tsukinari.repository.USERS_COLLECTION_REF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//copy code from DatabaseRepository.kt if needed
class AdminRepository {
    //load user list
    private val fictionsRef = Firebase.database.getReference(FICTIONS_COLLECTION_REF)
    private val chaptersRef = Firebase.database.getReference(CHAPTERS_COLLECTION_REF)
    private val followsRef = Firebase.database.getReference(FOLLOWS_COLLECTION_REF)
    private val fictionImagesRef = Firebase.storage.reference.child(IMAGES_COLLECTION_REF)
    private val commentsRef = Firebase.database.getReference(COMMENTS_COLLECTION_REF)
    private val usersRef = Firebase.database.getReference(USERS_COLLECTION_REF)
    private val statsRef = Firebase.database.getReference(STATS_COLLECTION_REF)

    val user = Firebase.auth.currentUser


    //is Admin
    fun isAdmin(): Boolean {
        return user?.uid == "QqJw3JL74ycUxz7HXBLg9aZp7IB2"
    }


    //get all user using Resource

    suspend fun getAllUsers(): Resources<List<UserModel>> {
        return try {
            val snapshot = usersRef.get().await()
            val fictions = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
            Resources.Success(data = fictions)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }

    //get all fictions of the user using Resource
    suspend fun getAllFictionsOfUser(userId: String): Resources<List<FictionModel>> {
        return try {
            val snapshot = fictionsRef.orderByChild("userId").equalTo(userId).get().await()
            val fictions = snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
            Resources.Success(data = fictions)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }

    //get all chapters of the fiction using Resource
    suspend fun getAllChaptersOfFiction(fictionId: String): Resources<List<ChapterModel>> {
        return try {
            val snapshot = chaptersRef.orderByChild("fictionId").equalTo(fictionId).get().await()
            val chapters = snapshot.children.mapNotNull { it.getValue(ChapterModel::class.java) }
            Resources.Success(data = chapters)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }

    //get all unverified fictions using Resource
    suspend fun getAllUnverifiedFictions(): Resources<List<FictionModel>> {
        return try {
            val snapshot = fictionsRef.orderByChild("verified").equalTo(false).get().await()
            val fictions = snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
            Resources.Success(data = fictions)
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }

    //get user info by id, dont use Resource, dont use suspend, use onSucess and onError
    fun getUserInfoById(
        userId: String,
        onSuccess: (UserModel) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    onSuccess(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }

    fun uploadCover(
        userId: String,
        imageUri: Uri,
        onComplete: (String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val filePath = fictionImagesRef.child(userId).child("profilePic")
        filePath.putFile(imageUri)
            .addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener { imageUrl ->
                    onComplete(imageUrl.toString())
                }
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    //update user info, dont use Resource, dont use suspend, use onSucess and onError
    fun updateUserInfo(
        userId: String,
        imageUri: Uri,
        user: UserModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (imageUri != Uri.EMPTY) {
            uploadCover(userId, imageUri, onComplete = {
                val newUser = user.copy(profileImageUrl = it!!)
                usersRef.child(userId).setValue(newUser).addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(it.exception?.message ?: "Unknown error")
                    }
                }
            }, onError = {
                onError(it.message ?: "Unknown error")
            })
        } else {
            usersRef.child(userId).setValue(user).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onError(it.exception?.message ?: "Unknown error")
                }
            }
        }
    }


    //delete user, dont use Resource, dont use suspend, use onSucess and onError, (change the user status to inactive)
    fun deleteUser(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        usersRef.child(userId).child("accountActive").setValue(false).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                onError(it.exception?.message ?: "Unknown error")
            }
        }
    }


    //re-enable user, dont use Resource, dont use suspend, use onSucess and onError, (change the user status to active)
    fun reEnableUser(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        usersRef.child(userId).child("accountActive").setValue(true).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                onError(it.exception?.message ?: "Unknown error")
            }
        }
    }

    //get fiction info by id, dont use Resource, dont use suspend, use onSucess and onError
    fun getFiction(
        fictionId: String,
        onSuccess: (FictionModel?) -> Unit,
        onError: () -> Unit
    ) {
        fictionsRef.child(fictionId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fiction = snapshot.getValue(FictionModel::class.java)
                onSuccess(fiction)
            }

            override fun onCancelled(error: DatabaseError) {
                onError()
            }
        })
    }

    suspend fun checkUsername(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            usersRef.orderByChild("username").equalTo(username).get().await().value != null
        }
    }


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

    //verify fiction
    fun verifyFiction(fictionId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        fictionsRef.child(fictionId).child("verified").setValue(true).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                onError(it.exception?.message ?: "Unknown error")
            }
        }
    }

    //get full stats of all the fiction, all the chapters, all the comments, all the users, all the verified fictions, all the unverified fictions, all the likes, all the dislikes, all the active users, all the inactive users, return the AllStatsModel, just get the count of each
    suspend fun getFullStats(): Resources<AllStatsModel> {
        return try {
            val fictions = fictionsRef.get().await().childrenCount
            val chapters = chaptersRef.get().await().childrenCount
            val comments = commentsRef.get().await().childrenCount
            val users = usersRef.get().await().childrenCount
            val verifiedFictions =
                fictionsRef.orderByChild("verified").equalTo(true).get().await().childrenCount
            val unverifiedFictions =
                fictionsRef.orderByChild("verified").equalTo(false).get().await().childrenCount
            val likes = statsRef.orderByChild("likedBy").get().await().childrenCount
            val dislikes = statsRef.orderByChild("dislikedBy").get().await().childrenCount
            val activeUsers =
                usersRef.orderByChild("accountActive").equalTo(true).get().await().childrenCount
            val inactiveUsers =
                usersRef.orderByChild("accountActive").equalTo(false).get().await().childrenCount

            val fictionsSnapshot = fictionsRef.get().await()
            var totalViews = 0L
            for (fictionSnapshot in fictionsSnapshot.children) {
                val fictionId = fictionSnapshot.key
                val chaptersSnapshot =
                    chaptersRef.orderByChild("fictionId").equalTo(fictionId).get().await()
                for (chapterSnapshot in chaptersSnapshot.children) {
                    val chapterId = chapterSnapshot.key
                    val viewsSnapshot =
                        statsRef.child(fictionId!!).child("chapters").child(chapterId!!)
                            .child("viewedBy").get().await()
                    totalViews += viewsSnapshot.childrenCount
                }
            }

            Resources.Success(
                data = AllStatsModel(
                    totalUsers = users,
                    totalFictions = fictions,
                    totalViews = totalViews,
                    totalChapters = chapters,
                    totalComments = comments,
                    totalVerifiedFictions = verifiedFictions,
                    totalUnverifiedFictions = unverifiedFictions,
                    totalLikes = likes,
                    totalDislikes = dislikes,
                    totalActiveUser = activeUsers,
                    totalInactiveUser = inactiveUsers
                )
            )
        } catch (e: Exception) {
            Resources.Error(throwable = e)
        }
    }

    //the same as getFullStats but for the user, return the Resource<UserStatsModel>, just get the count of each


}





