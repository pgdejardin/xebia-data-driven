package fr.xebia.api.directory.users.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.api.directory.users.noEvents
import fr.xebia.api.directory.users.users
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

internal class S3UsersStoreTest {

    private val bucketName = "s3-bucket"
    private val bucketKeyPrefix = "directory-extract"

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3UsersStore = S3UsersStore(amazonS3, bucketName, bucketKeyPrefix)

    @Test
    @DisplayName("bucket key should contain bucket key prefix, users")
    fun testBucketKey() {

        // when
        s3UsersStore.store(noEvents)

        // then
        verify(amazonS3).putObject(bucketName, expectedBucketKey(), "[]")
    }

    @Test
    @DisplayName("bucket content should be directory users serialized as JSON")
    fun testBucketObjectContent() {

        // when
        s3UsersStore.store(users)

        // then
        val expectedJSON = "[" +
            """{"id":"1","email":"email1@xebia.fr","givenName":"first name 1","familyName":"family 1","fullName":"full 1","photoUrl":"url 1"},""" +
            """{"id":"2","email":"email2@xebia.fr","givenName":"first name 2","familyName":"family 2","fullName":"full 2","photoUrl":"url 2"}""".trimMargin() +
            "]"
        verify(amazonS3).putObject(bucketName, expectedBucketKey(), expectedJSON)
    }

    private fun expectedBucketKey() =
        """$bucketKeyPrefix/users.json"""
}
