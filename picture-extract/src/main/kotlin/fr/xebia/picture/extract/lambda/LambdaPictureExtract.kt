package fr.xebia.picture.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import fr.xebia.picture.extract.PictureExtract
import fr.xebia.picture.extract.queue.PictureQueue
import fr.xebia.picture.extract.queue.sns.SNSPictureQueue
import fr.xebia.picture.extract.source.google.GoogleDrivePictureSource

class LambdaPictureExtract : RequestHandler<Any?, Unit> {

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val pictureSource = pictureSource()
        val pictureQueue = pictureQueue()

        PictureExtract(pictureSource, pictureQueue).extractToQueue()
    }

    private fun pictureSource(): GoogleDrivePictureSource {

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

    private fun pictureQueue(): PictureQueue {

        val topicArn = "TOPIC_ARN".env()

        return SNSPictureQueue(topicArn)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
