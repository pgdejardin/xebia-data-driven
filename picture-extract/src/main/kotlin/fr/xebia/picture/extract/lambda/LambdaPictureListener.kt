package fr.xebia.picture.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.picture.extract.PictureRef
import fr.xebia.picture.extract.PictureStorage
import fr.xebia.picture.extract.source.PictureSource
import fr.xebia.picture.extract.source.google.GoogleDrivePictureSource
import fr.xebia.picture.extract.source.google.credentials.GoogleCredentialSource
import fr.xebia.picture.extract.source.google.credentials.s3.S3GoogleCredentialSource
import fr.xebia.picture.extract.store.PictureStore
import fr.xebia.picture.extract.store.s3.S3PictureStore

class LambdaPictureListener : RequestHandler<SNSEvent, Any> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

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

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

    private fun googleCredentialSource(): GoogleCredentialSource {

        val credentialBucket = "CREDENTIAL_BUCKET".env()
        val credentialKey = "CREDENTIAL_KEY".env()

        return S3GoogleCredentialSource(amazonS3, credentialBucket, credentialKey)
    }

    private fun pictureSource(): PictureSource {

        val parentFolderId = "PARENT_FOLDER_ID".env()
        val mimeType = "MIME_TYPE".env()
        val pageSize = "PAGE_SIZE".env().toInt()

        val googleCredentialSource = googleCredentialSource()

        return GoogleDrivePictureSource(parentFolderId, mimeType, pageSize, googleCredentialSource.find())
    }

    private fun pictureStore(): PictureStore {

        val mimeType = "MIME_TYPE".env()
        val storeBucket = "STORE_BUCKET".env()
        val storeKey = "STORE_KEY".env()

        return S3PictureStore(mimeType, amazonS3, storeBucket, storeKey)
    }

}
