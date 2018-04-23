package fr.xebia.api.directory.users.store

import fr.xebia.api.directory.users.DirectoryUser
import java.time.LocalDate

interface UsersStore {

    fun store(directoryUsers: List<DirectoryUser>)

}
