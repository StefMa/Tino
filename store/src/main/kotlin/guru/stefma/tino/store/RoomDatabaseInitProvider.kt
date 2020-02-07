package guru.stefma.tino.store

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.room.Room

internal class RoomDatabaseInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val db = Room.databaseBuilder(
            context!!.applicationContext,
            LocalStore.AppDatabase::class.java,
            "tino-local-store"
        )
            .fallbackToDestructiveMigration() // Recreate themselves instead of crashing
            .build()

        LocalStore.DatabaseHolder.database = db
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw IllegalAccessException("This ContentProvider isn't indented to use")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw IllegalAccessException("This ContentProvider isn't indented to use")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw IllegalAccessException("This ContentProvider isn't indented to use")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalAccessException("This ContentProvider isn't indented to use")
    }

    override fun getType(uri: Uri): String? {
        throw IllegalAccessException("This ContentProvider isn't indented to use")
    }

}