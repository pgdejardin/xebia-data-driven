package fr.xebia.picture.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.picture.extract.PictureExtract
import fr.xebia.picture.extract.queue.PictureQueue
import fr.xebia.picture.extract.queue.sns.SNSPictureQueue
import fr.xebia.picture.extract.source.google.GoogleDrivePictureSource
import fr.xebia.picture.extract.source.google.credentials.GoogleCredentialSource
import fr.xebia.picture.extract.source.google.credentials.s3.S3GoogleCredentialSource

class LambdaPictureExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val pictureSource = pictureSource()
        val pictureQueue = pictureQueue()

        PictureExtract(pictureSource, pictureQueue).extractToQueue()
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

    private fun googleCredentialSource(): GoogleCredentialSource {

        val credentialBucket = "CREDENTIAL_BUCKET".env()
        val credentialKey = "CREDENTIAL_KEY".env()

        return S3GoogleCredentialSource(amazonS3, credentialBucket, credentialKey)
    }

    private fun pictureSource(): GoogleDrivePictureSource {

        val parentFolderId = "PARENT_FOLDER_ID".env()
        val mimeType = "MIME_TYPE".env()
        val pageSize = "PAGE_SIZE".env().toInt()

        val googleCredentialSource = googleCredentialSource()

        return GoogleDrivePictureSource(parentFolderId, mimeType, pageSize, googleCredentialSource.find())
    }

    private fun pictureQueue(): PictureQueue {

        val topicArn = "TOPIC_ARN".env()

        return SNSPictureQueue(topicArn)
    }

}
