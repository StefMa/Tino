package guru.stefma.tino.store

import androidx.room.*

internal class LocalStore : Store {

    object DatabaseHolder {
        lateinit var database: AppDatabase
    }

    override suspend fun store(data: Store.Data) {
        val dao = DatabaseHolder.database.dataDao()
        dao.insertData(data.toLocalData())
    }

    override suspend fun getAll(uid: UID): List<Store.Data> {
        if (uid == "2") {
            return listOf(
                Store.Data(
                    uid = "2",
                    appName = "guru.stefma.tino.debug",
                    notificationPostedAt = NotificationPostedTime(System.currentTimeMillis() / 1000 - 60),
                    notificationRemoveAt = NotificationRemovedTime(System.currentTimeMillis() / 1000)
                ),
                Store.Data(
                    uid = "2",
                    appName = "guru.stefma.tino.debug.more",
                    notificationPostedAt = NotificationPostedTime(System.currentTimeMillis() / 1000 - 300),
                    notificationRemoveAt = NotificationRemovedTime(System.currentTimeMillis() / 1000)
                ),
                Store.Data(
                    uid = "2",
                    appName = "guru.stefma.tino.debug.even.more",
                    notificationPostedAt = NotificationPostedTime(System.currentTimeMillis() / 1000 - 120),
                    notificationRemoveAt = NotificationRemovedTime(System.currentTimeMillis() / 1000)
                )
            )
        }
        val localData = DatabaseHolder.database.dataDao().getAll(uid)
        return localData.map { it.toStoreData(uid) }
    }

    override suspend fun setUserName(uid: UID, username: UserName) {
        if (username == "Tom") throw UserNameAlreadyTakenException(username)

        val accountDao = DatabaseHolder.database.accountDao()
        val localAccount = accountDao.getAccount(uid)
            ?.copy(username = username)
            ?: LocalAccount(
                uid = uid,
                username = username,
                creationdate = 0
            )
        accountDao.insertAccount(localAccount)
    }

    override suspend fun getUserName(uid: UID): UserName =
        DatabaseHolder.database.accountDao().getAccount(uid)?.username
            ?: throw UserWithUidDoesNotExist(uid)

    override suspend fun getUid(username: UserName): UID =
        if (username == "StefMa") "2" else throw UserWithNameDoesNotExist(username)

    override suspend fun setCreationDate(uid: UID, unixtimestamp: Long) {
        val accountDao = DatabaseHolder.database.accountDao()
        val localAccount = accountDao.getAccount(uid)
            ?.copy(creationdate = unixtimestamp)
            ?: LocalAccount(
                uid = uid,
                username = "",
                creationdate = 0
            )
        accountDao.insertAccount(localAccount)
    }

    override suspend fun getCreationDate(uid: UID): Long =
        if (uid == "2") 1583695312L
        else DatabaseHolder.database.accountDao().getAccount(uid)?.creationdate
            ?: throw UserWithUidDoesNotExist(uid)

    private fun Store.Data.toLocalData() = LocalData(
        uid = this.uid,
        appName = this.appName,
        notificationPostedAt = this.notificationPostedAt.unixtimestamp,
        notificationRemovedAt = this.notificationRemoveAt.unixtimestamp
    )

    private fun LocalData.toStoreData(uid: UID) = Store.Data(
        uid = uid,
        appName = this.appName,
        notificationPostedAt = NotificationPostedTime(this.notificationPostedAt),
        notificationRemoveAt = NotificationRemovedTime(this.notificationRemovedAt)
    )

    @Entity(tableName = "localData")
    data class LocalData(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val uid: String,
        val appName: String,
        val notificationPostedAt: Long,
        val notificationRemovedAt: Long
    )

    @Entity(tableName = "localAccount")
    data class LocalAccount(
        @PrimaryKey val id: Int = 1,
        val uid: String,
        val username: String,
        val creationdate: Long
    )

    @Dao
    interface LocalDataDao {
        @Insert
        fun insertData(data: LocalData)

        @Query("SELECT * FROM localData WHERE uid = :uid")
        fun getAll(uid: UID): List<LocalData>
    }

    @Dao
    interface LocalAccountDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertAccount(account: LocalAccount)

        @Query("SELECT * FROM localAccount WHERE uid = :uid")
        fun getAccount(uid: UID): LocalAccount?
    }

    @Database(
        entities = [
            LocalData::class,
            LocalAccount::class
        ],
        version = 2
    )
    abstract class AppDatabase : RoomDatabase() {
        abstract fun dataDao(): LocalDataDao
        abstract fun accountDao(): LocalAccountDao
    }
}