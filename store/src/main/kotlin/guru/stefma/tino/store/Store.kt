package guru.stefma.tino.store

import com.google.firebase.firestore.FirebaseFirestore

inline class NotificationPostedTime(val unixtimestamp: Long)
inline class NotificationRemovedTime(val unixtimestamp: Long)

typealias UID = String
typealias UserName = String

class UserNameAlreadyTakenException(username: UserName) : Error("UserName '$username' already taken!")
class UserWithNameDoesNotExist(username: UserName) : Error("User with userName '$username' does not exist!")
class UserWithUidDoesNotExist(uid: UID) : Error("User with uid '$uid' does not exist!")

interface Store {

    companion object {
        /**
         * Creates a new [Store] object where you can store data.
         *
         * You can set [local] to `true` to save the data only locally.
         * Otherwise it will be stored somewhere in the cloud.
         */
        operator fun invoke(local: Boolean = false): Store =
            if (local) LocalStore() else CloudStore(FirebaseFirestore.getInstance())
    }

    suspend fun store(data: Data)

    suspend fun getAll(uid: UID): List<Data>

    suspend fun setUserName(uid: UID, username: UserName)

    suspend fun getUserName(uid: UID): UserName

    suspend fun getUid(username: UserName): UID

    suspend fun setCreationDate(uid: UID, unixtimestamp: Long)

    suspend fun getCreationDate(uid: UID): Long

    data class Data(
        val uid: String,
        val appName: String,
        val notificationPostedAt: NotificationPostedTime,
        val notificationRemoveAt: NotificationRemovedTime
    )
}