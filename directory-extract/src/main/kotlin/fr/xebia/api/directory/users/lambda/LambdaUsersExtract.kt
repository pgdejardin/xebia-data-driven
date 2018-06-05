package fr.xebia.api.directory.users.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.api.directory.users.UsersExtract
import fr.xebia.api.directory.users.source.UsersSource
import fr.xebia.api.directory.users.source.google.GoogleUsersSource
import fr.xebia.api.directory.users.source.google.credentials.GoogleCredentialSource
import fr.xebia.api.directory.users.source.google.credentials.s3.S3GoogleCredentialSource
import fr.xebia.api.directory.users.store.UsersStore
import fr.xebia.api.directory.users.store.s3.S3UsersStore

class LambdaUsersExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val usersSource = usersSource()
        val usersStore = usersStore()

        val domain = "DOMAIN".env()

        val usersExtract = UsersExtract(usersSource, usersStore)

        usersExtract.extract(domain)
    }

    private fun usersSource(): UsersSource {

        val credentials = googleDirectoryCredential()

        return GoogleUsersSource(credentials)
    }

    private fun googleDirectoryCredential(): GoogleCredentialSource {

        val credentialBucket = "CREDENTIAL_BUCKET".env()
        val credentialKey = "CREDENTIAL_KEY".env()

        val serviceAccountId = "SERVICE_ACCOUNT_ID".env()
        val serviceAccountUser = "SERVICE_ACCOUNT_USER".env()
        val serviceAccountKeyAlias = "SERVICE_ACCOUNT_KEY_ALIAS".env()
        val serviceAccountKeyPassword = "SERVICE_ACCOUNT_KEY_PASSWORD".env()

        return S3GoogleCredentialSource(
            amazonS3,
            credentialBucket,
            credentialKey,
            serviceAccountId,
            serviceAccountUser,
            serviceAccountKeyAlias,
            serviceAccountKeyPassword
        )
    }

    private fun usersStore(): UsersStore {

        val storeBucket = "STORE_BUCKET".env()
        val storeKey = "STORE_KEY".env()

        return S3UsersStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
