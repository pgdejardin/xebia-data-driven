package fr.xebia.api.xke.calendar.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.api.xke.calendar.januaryEvents
import fr.xebia.api.xke.calendar.noEvents
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class S3CalendarStoreTest {

    private val bucketName = "s3-bucket"
    private val bucketKeyPrefix = "calendar-extract"
    private val bucketKeyExtractDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val bucketKeyCalendarDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3CalendarStore = S3CalendarStore(amazonS3, bucketName, bucketKeyPrefix)

    @Test
    @DisplayName("bucket key should contain bucket key prefix, extract date and calendar date")
    fun testBucketKey() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)
        val calendarDate = LocalDate.of(2018, 4, 1)

        // when
        s3CalendarStore.store(extractDate, calendarDate, noEvents)

        // then
        verify(amazonS3).putObject(bucketName, expectedBucketKey(extractDate, calendarDate), "[]")
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
            """{"id":"id1","startTime":"2018-01-01T10:00:00","endTime":"2018-01-01T11:00:00","summary":"summary1","description":"description1"},""" +
            """{"id":"id2","startTime":"2018-01-01T11:00:00","endTime":"2018-01-01T12:00:00","summary":"summary2","description":"description2"}""" +
            "]"
        verify(amazonS3).putObject(bucketName, expectedBucketKey(extractDate, calendarDate), expectedJSON)
    }

    private fun expectedBucketKey(extractDate: LocalDate, calendarDate: LocalDate) =
        """$bucketKeyPrefix/${extractDate.format(bucketKeyExtractDateFormatter)}/${calendarDate.format(bucketKeyCalendarDateFormatter)}.json"""
}
