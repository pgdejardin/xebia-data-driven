package fr.xebia.user.extract

import fr.xebia.user.extract.source.UsersSource
import fr.xebia.user.extract.store.UsersStore
import java.time.LocalDate

data class DirectoryUser(val id: String,
                         val email: String,
                         val givenName: String,
                         val familyName: String,
                         val fullName: String,
                         val photoUrl: String?)

class UsersExtract(private val usersSource: UsersSource,
                   private val usersStore: UsersStore) {

    fun extract() {

        val directoryUsers = usersSource.find()

        usersStore.store(LocalDate.now(), directoryUsers)
    }

}
