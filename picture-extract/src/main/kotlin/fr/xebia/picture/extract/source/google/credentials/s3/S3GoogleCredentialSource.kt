package fr.xebia.picture.extract.source.google.credentials.s3

import com.amazonaws.services.s3.AmazonS3
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import fr.xebia.picture.extract.source.google.credentials.GoogleCredentialSource

class S3GoogleCredentialSource(private val amazonS3: AmazonS3,
                               private val bucketName: String,
                               private val key: String) : GoogleCredentialSource {

    override fun find(): GoogleCredential = GoogleCredential.fromStream(amazonS3.getObject(bucketName, key).objectContent)

}
