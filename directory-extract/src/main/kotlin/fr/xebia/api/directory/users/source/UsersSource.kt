package fr.xebia.api.directory.users.source

import fr.xebia.api.directory.users.DirectoryUser

interface UsersSource {

    fun find(): List<DirectoryUser>

}
