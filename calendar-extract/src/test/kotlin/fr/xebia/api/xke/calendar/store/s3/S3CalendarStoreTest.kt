package fr.xebia.api.xke.calendar.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.api.xke.calendar.CalendarEvent
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class S3CalendarStoreTest {

    private val bucketName = "s3-bucket"
    private val bucketKeyPrefix = "calendar-extract"
    private val bucketKeySuffixFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    private val s3 = mock(AmazonS3::class.java)
    private val s3CalendarStore = S3CalendarStore(s3, bucketName, bucketKeyPrefix)

    @Test
    @DisplayName("bucket key should be bucket key prefix with calendar date formatted as 'yyyy-MM'")
    fun testBucketKey() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)
        val calendarDate = LocalDate.of(2018, 4, 1)
        val calendarEvents = emptyList<CalendarEvent>()

        // when
        s3CalendarStore.store(extractDate, calendarDate, calendarEvents)

        // then
        verify(s3).putObject(bucketName, calendarDate.expectedBucketKey(), "[]")
    }

    @Test
    @DisplayName("bucket content should be calendar events serialized in JSON")
    fun testBucketObjectContent() {

        // given
        val extractDate = LocalDate.of(2018, 1, 1)
        val calendarDate = LocalDate.of(2018, 4, 1)
        val calendarEvents = listOf(
            CalendarEvent("summary1", "description1"),
            CalendarEvent("summary2", "description2")
        )

        // when
        s3CalendarStore.store(extractDate, calendarDate, calendarEvents)

        // then
        val expectedJSON = "[" +
            """{"summary":"summary1","description":"description1"},""" +
            """{"summary":"summary2","description":"description2"}""" +
            "]"
        verify(s3).putObject(bucketName, calendarDate.expectedBucketKey(), expectedJSON)
    }

    private fun LocalDate.expectedBucketKey() = """$bucketKeyPrefix/${format(bucketKeySuffixFormatter)}.json"""
}
