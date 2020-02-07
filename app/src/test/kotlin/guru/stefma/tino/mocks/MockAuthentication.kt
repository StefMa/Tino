package guru.stefma.tino.mocks

import guru.stefma.tino.authentication.Authentication

open class MockAuthentication : Authentication {
    override val uid: String = ""

    override suspend fun createAnonymousUser(): Unit = Unit

}