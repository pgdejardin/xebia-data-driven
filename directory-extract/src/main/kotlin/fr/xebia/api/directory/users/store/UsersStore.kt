package fr.xebia.api.directory.users.store

import fr.xebia.api.directory.users.DirectoryUser
import java.time.LocalDate

interface UsersStore {

    fun store(extractDate: LocalDate, directoryUsers: List<DirectoryUser>)

}
