package com.meow.tsukinari.presentation.admin

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import com.meow.tsukinari.repository.CHAPTERS_COLLECTION_REF
import com.meow.tsukinari.repository.COMMENTS_COLLECTION_REF
import com.meow.tsukinari.repository.FICTIONS_COLLECTION_REF
import com.meow.tsukinari.repository.FOLLOWS_COLLECTION_REF
import com.meow.tsukinari.repository.IMAGES_COLLECTION_REF
import com.meow.tsukinari.repository.STATS_COLLECTION_REF
import com.meow.tsukinari.repository.USERS_COLLECTION_REF

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


}