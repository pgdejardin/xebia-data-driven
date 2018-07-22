package fr.xebia.user.extract.source

import fr.xebia.user.extract.DirectoryUser

interface UsersSource {

    fun find(): List<DirectoryUser>

}
