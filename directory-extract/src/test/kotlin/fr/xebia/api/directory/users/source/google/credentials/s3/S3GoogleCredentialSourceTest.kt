package fr.xebia.api.directory.users.source.google.credentials.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.util.StringInputStream
import org.apache.http.client.methods.HttpGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

internal class S3GoogleCredentialSourceTest {

    private val bucketName = "s3-bucket"
    private val bucketKey = "directory-extract"

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3GoogleCredentialSource = S3GoogleCredentialSource(amazonS3, bucketName, bucketKey)

    @Test
    @DisplayName("object content should match stream returned for given bucket and key")
    fun testBucketObjectContent() {

        val expectedContentStream = S3ObjectInputStream(StringInputStream(""), HttpGet())
        val s3Object = mock(S3Object::class.java)
        given(s3Object.objectContent)
            .willReturn(expectedContentStream)
        given(amazonS3.getObject(bucketName, bucketKey))
            .willReturn(s3Object)

        val result = s3GoogleCredentialSource.find()

        assertEquals(expectedContentStream, result)
    }

}
