package fr.xebia.api.directory.users.source.google

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY
import fr.xebia.api.directory.users.DirectoryUser
import fr.xebia.api.directory.users.source.UsersSource
import fr.xebia.api.directory.users.source.google.credentials.GoogleCredentialSource

class GoogleUsersSource(private val googleCredentialSource: GoogleCredentialSource) : UsersSource {

    private val directory by lazy(::getDirectoryService)

    override fun find(domain : String): List<DirectoryUser> {

        return directory.users()
            .list()
            .setDomain(domain)
            .setOrderBy("email")
            .execute()
            .users
            .map {
                DirectoryUser(
                    email = it.primaryEmail,
                    familyName = it.name.familyName,
                    fullName = it.name.fullName
                )
            }
    }

    private fun getDirectoryService(): Directory {

        val inputStream = googleCredentialSource.find()

        val credential = GoogleCredential.fromStream(inputStream)
            .createScoped(listOf(ADMIN_DIRECTORY_USER_READONLY))

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

        return Directory.Builder(httpTransport, jacksonFactory, credential)
            .setApplicationName("xdd-direcory-api")
            .build()
    }
}
