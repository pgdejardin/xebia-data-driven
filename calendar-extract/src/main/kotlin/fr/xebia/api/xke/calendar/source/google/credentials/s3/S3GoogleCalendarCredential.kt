package fr.xebia.api.xke.calendar.source.google.credentials.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.api.xke.calendar.source.google.credentials.GoogleCalendarCredential
import java.io.InputStream

class S3GoogleCalendarCredential(private val amazonS3: AmazonS3,
                                 private val bucketName: String,
                                 private val key: String) : GoogleCalendarCredential {

    override fun find(): InputStream = amazonS3.getObject(bucketName, key).objectContent

}
