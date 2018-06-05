package fr.xebia.api.directory.users.store

import fr.xebia.api.directory.users.DirectoryUser

interface UsersStore {

    fun store(directoryUsers: List<DirectoryUser>)

}
