package fr.xebia.user.extract.store

import fr.xebia.user.extract.DirectoryUser
import java.time.LocalDate

interface UsersStore {

    fun store(extractDate: LocalDate, directoryUsers: List<DirectoryUser>)

}
