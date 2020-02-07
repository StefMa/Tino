package guru.stefma.tino.mocks

import guru.stefma.tino.store.Store
import guru.stefma.tino.store.UID
import guru.stefma.tino.store.UserName

open class MockStore : Store {
    override suspend fun store(data: Store.Data) = Unit

    override suspend fun getAll(uid: UID): List<Store.Data> = emptyList()

    override suspend fun setUserName(uid: UID, username: UserName) = Unit

    override suspend fun getUserName(uid: UID): UserName = ""

    override suspend fun getUid(username: UserName): UID = ""

    override suspend fun setCreationDate(uid: UID, unixtimestamp: Long) = Unit

    override suspend fun getCreationDate(uid: UID): Long = 0

}