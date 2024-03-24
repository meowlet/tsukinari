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

    //update user info, dont use Resource, dont use suspend, use onSucess and onError
    fun updateUserInfo(
        userId: String,
        user: UserModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                onError(it.exception?.message ?: "Unknown error")
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


}





