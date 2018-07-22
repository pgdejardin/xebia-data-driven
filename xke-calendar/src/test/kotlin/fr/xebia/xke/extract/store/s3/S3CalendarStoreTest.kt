package fr.xebia.xke.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.xke.extract.januaryEvents
import fr.xebia.xke.extract.noEvents
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate

internal class S3CalendarStoreTest {

    private val bucketName = "s3-bucket"
    private val bucketKey = "calendar-extract"

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3CalendarStore = S3CalendarStore(amazonS3, bucketName, bucketKey)

    @Test
    @DisplayName("bucket key should contain bucket key prefix, extract date and calendar date")
    fun testBucketKey() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)
        val calendarDate = LocalDate.of(2018, 4, 1)

        // when
        s3CalendarStore.store(extractDate, calendarDate, noEvents)

        // then
        verify(amazonS3).putObject(bucketName, "$bucketKey/2018/01/01/2018-04.json", "[]")
    }

    @Test
    @DisplayName("bucket content should be calendar events serialized as JSON")
    fun testBucketObjectContent() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)
        val calendarDate = LocalDate.of(2018, 4, 1)

        // when
        s3CalendarStore.store(extractDate, calendarDate, januaryEvents)

        // then
        val expectedJSON = "[" +
            """{"id":"id1","startTime":"2018-01-01T10:00:00","endTime":"2018-01-01T11:00:00","summary":"summary1","description":"description1","attendees":["mail1@test.com","mail2@test.com"]},""" +
            """{"id":"id2","startTime":"2018-01-01T11:00:00","endTime":"2018-01-01T12:00:00","summary":"summary2","description":"description2","attendees":["mail3@test.com","mail4@test.com"]}""" +
            "]"
        verify(amazonS3).putObject(bucketName, "$bucketKey/2018/01/01/2018-04.json", expectedJSON)
    }

}
