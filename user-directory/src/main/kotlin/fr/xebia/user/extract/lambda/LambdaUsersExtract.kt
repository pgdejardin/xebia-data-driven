package fr.xebia.user.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import fr.xebia.user.extract.UsersExtract
import fr.xebia.user.extract.source.UsersSource
import fr.xebia.user.extract.source.google.GoogleUsersSource
import fr.xebia.user.extract.store.UsersStore
import fr.xebia.user.extract.store.s3.S3UsersStore

class LambdaUsersExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val usersSource = usersSource()
        val usersStore = usersStore()

        val usersExtract = UsersExtract(usersSource, usersStore)

        usersExtract.extract()
    }

    private fun usersSource(): UsersSource {

        val serviceAccountKey = "SERVICE_ACCOUNT_KEY".env()
        val serviceAccountRequest = GetSecretValueRequest().withSecretId(serviceAccountKey)
        val serviceAccount = amazonSecretManager.getSecretValue(serviceAccountRequest).secretString

        val serviceAccountUserKey = "SERVICE_ACCOUNT_USER_KEY".env()
        val serviceAccountUserRequest = GetParameterRequest().withName(serviceAccountUserKey)
        val serviceAccountUser = amazonSSM.getParameter(serviceAccountUserRequest).parameter.value

        val domain = "DOMAIN".env()
        val maxResults = "MAX_RESULTS".env().toInt()

        return GoogleUsersSource(serviceAccount, serviceAccountUser, domain, maxResults)
    }

    private fun usersStore(): UsersStore {

        val storeBucket = "STORE_BUCKET_NAME".env()
        val storeKey = "STORE_BUCKET_KEY".env()

        return S3UsersStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
