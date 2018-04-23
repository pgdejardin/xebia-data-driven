package fr.xebia.api.directory.users.local

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.api.directory.users.DirectoryUser
import fr.xebia.api.directory.users.UsersExtract
import fr.xebia.api.directory.users.source.google.GoogleUsersSource
import fr.xebia.api.directory.users.source.google.credentials.s3.S3GoogleCredentialSource
import fr.xebia.api.directory.users.store.UsersStore

val amazonS3: AmazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

fun main(args: Array<String>) {

    val googleDirectoryCredential = S3GoogleCredentialSource(amazonS3, args[0], args[1])
    val usersSource = GoogleUsersSource(googleDirectoryCredential)

    val usersStore = ConsoleUsersStore()

    val usersExtract = UsersExtract(usersSource, usersStore)

    usersExtract.extract("xebia.fr")
}

class ConsoleUsersStore : UsersStore {

    override fun store(directoryUsers: List<DirectoryUser>) {
        directoryUsers.forEach(::println)
    }

}
