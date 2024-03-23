package com.meow.tsukinari.presentation.admin

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
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
import kotlinx.coroutines.tasks.await

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

    //get all unverified fictions, return the List<FictionModel>, dont use Resource, dont suspend

    fun getAllUnverifiedFictions(
        onError: (Throwable?) -> Unit,
        onSuccess: (List<FictionModel>?) -> Unit
    ) {
        fictionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fictions =
                    snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
                        .filter { !it.verified }
                onSuccess(fictions)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    //get all verified fictions using Resource

}


