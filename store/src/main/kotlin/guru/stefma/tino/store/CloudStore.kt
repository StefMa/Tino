package guru.stefma.tino.store

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class CloudStore(
    private val firestore: FirebaseFirestore
) : Store {

    override suspend fun store(data: Store.Data): Unit = suspendCoroutine {
        firestore
            .collection("users")
            .document(data.uid)
            .collection("notifications")
            .document()
            .set(data.toFirestoreData())
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    it.resume(Unit)
                } else {
                    it.resumeWithException(taskResult.exception!!)
                }
            }
    }

    override suspend fun getAll(uid: UID): List<Store.Data> = suspendCoroutine {
        firestore
            .collection("users")
            .document(uid)
            .collection("notifications")
            .get()
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    val dataList =
                        taskResult.result?.map { it.data.toStoreData(uid) } ?: emptyList()
                    it.resume(dataList)
                } else {
                    it.resumeWithException(taskResult.exception!!)
                }
            }
    }

    override suspend fun setUserName(uid: UID, username: UserName) {
        if (isUserNameTaken(username)) {
            throw UserNameAlreadyTakenException(username)
        }

        return suspendCoroutine {
            firestore
                .collection("users")
                .document(uid)
                .set(mapOf("username" to username), SetOptions.merge())
                .addOnCompleteListener { taskResult ->
                    if (taskResult.isSuccessful) {
                        it.resume(Unit)
                    } else {
                        it.resumeWithException(taskResult.exception!!)
                    }
                }
        }
    }

    override suspend fun getUserName(uid: UID): UserName = suspendCoroutine {
        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    val username = taskResult.result!!.getString("username")
                    if (username == null) {
                        it.resumeWithException(UserWithUidDoesNotExist(uid))
                    } else {
                        it.resume(username)
                    }
                } else {
                    it.resumeWithException(taskResult.exception!!)
                }
            }
    }

    override suspend fun getUid(username: UserName): UID = suspendCoroutine {
        firestore
            .collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    val result = taskResult.result!!
                    if (result.size() == 0) {
                        it.resumeWithException(UserWithNameDoesNotExist(username))
                    } else {
                        it.resume(result.documents.first().reference.id)
                    }
                } else {
                    it.resumeWithException(taskResult.exception!!)
                }
            }
    }

    override suspend fun setCreationDate(uid: UID, unixtimestamp: Long): Unit = suspendCoroutine {
        firestore
            .collection("users")
            .document(uid)
            .set(mapOf("creationdate" to unixtimestamp), SetOptions.merge())
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    it.resume(Unit)
                } else {
                    it.resumeWithException(taskResult.exception!!)
                }
            }
    }

    override suspend fun getCreationDate(uid: UID): Long = suspendCoroutine {
        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    val creationdate = taskResult.result!!.getLong("creationdate")
                    if (creationdate == null) {
                        it.resumeWithException(UserWithUidDoesNotExist(uid))
                    } else {
                        it.resume(creationdate)
                    }
                } else {
                    it.resumeWithException(taskResult.exception!!)
                }
            }
    }

    private suspend fun isUserNameTaken(username: UserName): Boolean =
        try {
            getUid(username)
            true
        } catch (userDoesNotExist: UserWithNameDoesNotExist) {
            false
        }

    private fun Store.Data.toFirestoreData() = mapOf(
        "app" to appName,
        "postedAt" to notificationPostedAt.unixtimestamp,
        "removedAt" to notificationRemoveAt.unixtimestamp
    )

    private fun Map<String, Any>.toStoreData(uid: String) = Store.Data(
        uid = uid,
        appName = get("app") as String,
        notificationPostedAt = NotificationPostedTime(get("postedAt") as Long),
        notificationRemoveAt = NotificationRemovedTime(get("removedAt") as Long)
    )
}