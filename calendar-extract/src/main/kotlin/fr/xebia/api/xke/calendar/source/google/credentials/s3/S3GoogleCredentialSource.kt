package fr.xebia.api.xke.calendar.source.google.credentials.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.api.xke.calendar.source.google.credentials.GoogleCredentialSource
import java.io.InputStream

class S3GoogleCredentialSource(private val amazonS3: AmazonS3,
                               private val bucketName: String,
                               private val key: String) : GoogleCredentialSource {

    override fun find(): InputStream = amazonS3.getObject(bucketName, key).objectContent

}
