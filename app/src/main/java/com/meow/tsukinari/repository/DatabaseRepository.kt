package com.meow.tsukinari.repository

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.meow.tsukinari.model.FictionModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


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


    // Khởi tạo tham chiếu đến firebase realtime database
    private val databaseRef: DatabaseReference = Firebase
        .database.reference

    fun getUserFictionModel(
        userId: String,
    ): Flow<Resources<List<FictionModel>>> = callbackFlow {
        // Tạo một listener để lắng nghe sự thay đổi của dữ liệu
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kiểm tra snapshot có tồn tại hay không
                if (snapshot.exists()) {
                    // Lấy ra danh sách các ghi chú từ snapshot
                    val FictionModel =
                        snapshot.children.mapNotNull { it.getValue(FictionModel::class.java) }
                    // Gửi kết quả thành công với dữ liệu là danh sách ghi chú
                    trySend(Resources.Success(data = FictionModel))
                } else {
                    // Gửi kết quả lỗi với thông báo là không có dữ liệu
                    trySend(Resources.Error(throwable = Exception("No data")))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Gửi kết quả lỗi với nguyên nhân là error
                trySend(Resources.Error(throwable = error.toException()))
            }
        }

        // Thêm listener vào node chứa các ghi chú của người dùng
        databaseRef.child(FICTIONS_COLLECTION_REF).child(userId)
            .addValueEventListener(valueEventListener)

        // Khi flow bị huỷ, gỡ bỏ listener
        awaitClose {
            databaseRef.child(FICTIONS_COLLECTION_REF).child(userId)
                .removeEventListener(valueEventListener)
        }
    }

    fun getNote(
        noteId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (FictionModel?) -> Unit
    ) {
        // Lấy dữ liệu của ghi chú theo id
        databaseRef.child(FICTIONS_COLLECTION_REF).child(getUserId()).child(noteId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Gọi hàm onSuccess với dữ liệu là ghi chú từ snapshot
                    onSuccess.invoke(snapshot.getValue(FictionModel::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gọi hàm onError với nguyên nhân là error
                    onError.invoke(error.toException())
                }
            })
    }

    fun addNote(
        uploaderId: String,
        title: String,
        description: String,
        uploadedAt: Timestamp,
        onComplete: (Boolean) -> Unit,
    ) {
        val id = databaseRef.child(FICTIONS_COLLECTION_REF).push().key ?: "null"
        val note = FictionModel(
            uploaderId, title, description, uploadedAt
        )


        // Thêm fiction mới vào node tương ứng với id
        databaseRef.child(FICTIONS_COLLECTION_REF).child(id)
            .setValue(note)
            .addOnCompleteListener { result ->
                // Gọi hàm onComplete với kết quả là thành công hay thất bại
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteNote(noteId: String, onComplete: (Boolean) -> Unit) {
        // Xoá ghi chú khỏi node tương ứng với id
        databaseRef.child(FICTIONS_COLLECTION_REF).child(getUserId()).child(noteId)
            .removeValue()
            .addOnCompleteListener { result ->
                // Gọi hàm onComplete với kết quả là thành công hay thất bại
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun updateNote(
        title: String,
        note: String,
        color: Int,
        noteId: String,
        onResult: (Boolean) -> Unit
    ) {
        // Tạo một map chứa các dữ liệu cần cập nhật
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to note,
            "title" to title,
        )

        // Cập nhật dữ liệu cho node tương ứng với id
        databaseRef.child(FICTIONS_COLLECTION_REF).child(getUserId()).child(noteId)
            .updateChildren(updateData)
            .addOnCompleteListener { result ->
                // Gọi hàm onResult với kết quả là thành công hay thất bại
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









