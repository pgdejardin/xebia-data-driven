package fr.xebia.api.directory.users.source.google

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.SecurityUtils
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes
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
                        id = it.id,
                        email = it.primaryEmail,
                        givenName = it.name.givenName,
                        familyName = it.name.familyName,
                        fullName = it.name.fullName,
                        photoUrl = it.thumbnailPhotoUrl
                )
            }
    }

    private fun getDirectoryService(): Directory {

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

        val privateKey = SecurityUtils.loadPrivateKeyFromKeyStore(
                SecurityUtils.getPkcs12KeyStore(),
                googleCredentialSource.find(), "notasecret", "privatekey", "notasecret")

        val credential = GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jacksonFactory)
                .setServiceAccountId("xdd-directory-api@xdd-directory-api.iam.gserviceaccount.com")
                .setServiceAccountScopes(listOf(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY))
                .setServiceAccountUser("akinsella@xebia.fr")
                .setServiceAccountPrivateKey(privateKey)
                .build()

        return Directory.Builder(httpTransport, jacksonFactory, credential)
            .setApplicationName("xdd-directory-api")
            .build()
    }
}
