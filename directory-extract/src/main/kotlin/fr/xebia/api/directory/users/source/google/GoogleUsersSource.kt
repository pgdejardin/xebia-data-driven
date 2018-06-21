package fr.xebia.api.directory.users.source.google

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.PemReader
import com.google.api.client.util.SecurityUtils
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes
import fr.xebia.api.directory.users.DirectoryUser
import fr.xebia.api.directory.users.source.UsersSource
import java.io.StringReader
import java.security.spec.PKCS8EncodedKeySpec

class GoogleUsersSource(private val serviceAccount: String,
                        private val serviceAccountUser: String,
                        private val maxResults: Int) : UsersSource {

    private val directory by lazy(::buildDirectory)

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

    private fun buildDirectory(): Directory {

        val objectMapper = ObjectMapper()

        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jacksonFactory = JacksonFactory.getDefaultInstance()

        val jsonNode = objectMapper.readTree(serviceAccount)
        val serviceAccountId = jsonNode["client_email"].asText()
        val privateKey = jsonNode["private_key"].asText()
        val bytes = PemReader.readFirstSectionAndClose(StringReader(privateKey), "PRIVATE KEY").base64DecodedBytes
        val serviceAccountPrivateKey = SecurityUtils.getRsaKeyFactory().generatePrivate(PKCS8EncodedKeySpec(bytes))

        val googleCredential = GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jacksonFactory)
            .setServiceAccountId(serviceAccountId)
            .setServiceAccountUser(serviceAccountUser)
            .setServiceAccountPrivateKey(serviceAccountPrivateKey)
            .setServiceAccountScopes(listOf(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY))
            .build()

        return Directory.Builder(httpTransport, jacksonFactory, googleCredential)
            .setApplicationName("user-directory-extract")
            .build()
    }

}
