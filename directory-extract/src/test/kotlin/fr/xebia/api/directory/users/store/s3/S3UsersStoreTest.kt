package fr.xebia.api.directory.users.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.api.directory.users.noEvents
import fr.xebia.api.directory.users.users
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class S3UsersStoreTest {

    private val bucketName = "s3-bucket"
    private val bucketKeyPrefix = "directory-extract"
    private val bucketKeyExtractDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3UsersStore = S3UsersStore(amazonS3, bucketName, bucketKeyPrefix)

    @Test
    @DisplayName("bucket key should contain bucket key prefix, users")
    fun testBucketKey() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)

        // when
        s3UsersStore.store(extractDate, noEvents)

        // then
        verify(amazonS3).putObject(bucketName, expectedBucketKey(extractDate), "[]")
    }

    @Test
    @DisplayName("bucket content should be directory users serialized as JSON")
    fun testBucketObjectContent() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)

        // when
        s3UsersStore.store(extractDate, users)

        // then
        val expectedJSON = "[" +
            """{"id":"1","email":"email1@xebia.fr","givenName":"first name 1","familyName":"family 1","fullName":"full 1","photoUrl":"url 1"},""" +
            """{"id":"2","email":"email2@xebia.fr","givenName":"first name 2","familyName":"family 2","fullName":"full 2","photoUrl":"url 2"}""".trimMargin() +
            "]"
        verify(amazonS3).putObject(bucketName, expectedBucketKey(extractDate), expectedJSON)
    }

    private fun expectedBucketKey(extractDate: LocalDate) =
        """$bucketKeyPrefix/${extractDate.format(bucketKeyExtractDateFormatter)}/users.json"""
}
