package fr.xebia.picture.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import fr.xebia.picture.extract.PictureRef
import fr.xebia.picture.extract.PictureStorage
import fr.xebia.picture.extract.source.PictureSource
import fr.xebia.picture.extract.source.google.GoogleDrivePictureSource
import fr.xebia.picture.extract.store.PictureStore
import fr.xebia.picture.extract.store.s3.S3PictureStore

class LambdaPictureListener : RequestHandler<SNSEvent, Any> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: SNSEvent, context: Context) {

        println("received ${input.records} events")

        val pictureSource = pictureSource()
        val pictureStore = pictureStore()

        val pictureRefs = input.records.map {
            val parts = it.sns.message.split("|")
            PictureRef(parts[0], parts[1])
        }

        val pictureStorage = PictureStorage(pictureSource, pictureStore)

        pictureStorage.storeFromQueue(pictureRefs)
    }

    private fun pictureSource(): PictureSource {

        val parentFolderIdKey = "PARENT_FOLDER_ID_KEY".env()
        val parentFolderIdRequest = GetParameterRequest().withName(parentFolderIdKey)
        val parentFolderId = amazonSSM.getParameter(parentFolderIdRequest).parameter.value

        val mimeType = "MIME_TYPE".env()
        val pageSize = "PAGE_SIZE".env().toInt()

        val serviceAccountKey = "SERVICE_ACCOUNT_KEY".env()
        val serviceAccountRequest = GetSecretValueRequest().withSecretId(serviceAccountKey)
        val serviceAccount = amazonSecretManager.getSecretValue(serviceAccountRequest).secretString

        return GoogleDrivePictureSource(parentFolderId, mimeType, pageSize, serviceAccount)
    }

    private fun pictureStore(): PictureStore {

        val mimeType = "MIME_TYPE".env()
        val storeBucket = "STORE_BUCKET_NAME".env()
        val storeKey = "STORE_BUCKET_KEY".env()

        return S3PictureStore(mimeType, amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
