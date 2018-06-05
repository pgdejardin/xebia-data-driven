package fr.xebia.api.directory.users.source.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.Directory
import fr.xebia.api.directory.users.DirectoryUser
import fr.xebia.api.directory.users.source.UsersSource
import fr.xebia.api.directory.users.source.google.credentials.GoogleCredentialSource

class GoogleUsersSource(private val googleCredentialSource: GoogleCredentialSource,
                        private val maxResults : Int) : UsersSource {

    private val directory by lazy {

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

        Directory.Builder(httpTransport, jacksonFactory, googleCredentialSource.find())
            .setApplicationName("directory-extract")
            .build()
    }

    override fun find(domain: String): List<DirectoryUser> {

        return directory.users()
            .list()
            .setDomain(domain)
            .setOrderBy("email")
            .setMaxResults(maxResults)
            .execute()
            .users
            .map {
                DirectoryUser(
                    id = it.id,
                    email = it.primaryEmail,
                    givenName = it.name.givenName,
                    familyName = it.name.familyName,
                    fullName = it.name.fullName,
                    photoUrl = it.thumbnailPhotoUrl
                )
            }
    }

}
