package guru.stefma.tino.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface Authentication {
    companion object {
        operator fun invoke(): Authentication = DefaultAuthentication()
    }

    val uid: String
    suspend fun createAnonymousUser()
}

internal class DefaultAuthentication : Authentication {

    private val auth by lazy { FirebaseAuth.getInstance() }

    private var user: FirebaseUser? = null
        set(value) {
            field = value
        }
        get() = auth.currentUser

    override val uid: String
        get() = user?.uid ?: throw IllegalAccessException(
            "Can't get uid because user is null. " +
                    "Please create one first with 'createAnonymousUser'}"
        )

    override suspend fun createAnonymousUser() = suspendCoroutine<Unit> {
        if (user != null) {
            it.resume(Unit)
        } else {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resume(Unit)
                        user = task.result!!.user
                    } else {
                        it.resumeWithException(task.exception!!)
                    }
                }
        }
    }

}