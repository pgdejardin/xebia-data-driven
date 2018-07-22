package fr.xebia.hr.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.hr.extract.hrList
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate

class S3HRStoreTest {

    private val bucketName = "s3-bucket"
    private val bucketKey = "hr-sheet"

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3HRStore = S3HRStore(amazonS3, bucketName, bucketKey)

    @Test
    @DisplayName("bucket key should contain bucket key prefix and extract date")
    fun testBucketKey() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)

        // when
        s3HRStore.store(extractDate, emptyList())

        // then
        verify(amazonS3).putObject(bucketName, "$bucketKey/2018/01/01/hr.json", "[]")
    }

    @Test
    @DisplayName("bucket content should be hr list serialized as JSON")
    fun testBucketObjectContent() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)

        // when
        s3HRStore.store(extractDate, hrList)

        // then
        val expectedJSON = "[" +
            """{"lastName":"lastName1","firstName":"firstName1","xebiaStartDate":"2018-01-01","careerStartDate":"2018-02-01","email":"user1@email.com"},""" +
            """{"lastName":"lastName2","firstName":"firstName2","xebiaStartDate":"2018-01-02","careerStartDate":"2018-02-02","email":"user2@email.com"}""" +
            "]"
        verify(amazonS3).putObject(bucketName, "$bucketKey/2018/01/01/hr.json", expectedJSON)
    }

}
