package fr.xebia.api.directory.users

import fr.xebia.api.directory.users.source.UsersSource
import fr.xebia.api.directory.users.store.UsersStore

data class DirectoryUser(val id: String,
                         val email: String,
                         val givenName: String,
                         val familyName: String,
                         val fullName: String,
                         val photoUrl: String?)

class UsersExtract(private val usersSource: UsersSource,
                   private val usersStore: UsersStore) {

    fun extract(domain: String) {
        val directoryUsers = usersSource.find(domain)
        usersStore.store(directoryUsers)
    }

}
