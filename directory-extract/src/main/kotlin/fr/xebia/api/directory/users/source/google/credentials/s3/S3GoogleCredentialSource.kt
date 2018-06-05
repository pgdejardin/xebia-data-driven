package fr.xebia.api.directory.users.source.google.credentials.s3

import com.amazonaws.services.s3.AmazonS3
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.SecurityUtils
import com.google.api.services.admin.directory.DirectoryScopes
import fr.xebia.api.directory.users.source.google.credentials.GoogleCredentialSource

class S3GoogleCredentialSource(private val amazonS3: AmazonS3,
                               private val bucketName: String,
                               private val bucketKey: String,
                               private val serviceAccountId: String,
                               private val serviceAccountUser: String,
                               private val serviceAccountKeyAlias: String,
                               private val serviceAccountKeyPassword: String) : GoogleCredentialSource {

    override fun find(): GoogleCredential {

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

        val keyContent = amazonS3.getObject(bucketName, bucketKey).objectContent

        val serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(
            SecurityUtils.getPkcs12KeyStore(),
            keyContent,
            serviceAccountKeyPassword,
            serviceAccountKeyAlias,
            serviceAccountKeyPassword
        )

        return GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jacksonFactory)
            .setServiceAccountId(serviceAccountId)
            .setServiceAccountUser(serviceAccountUser)
            .setServiceAccountPrivateKey(serviceAccountPrivateKey)
            .setServiceAccountScopes(listOf(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY))
            .build()
    }

}
