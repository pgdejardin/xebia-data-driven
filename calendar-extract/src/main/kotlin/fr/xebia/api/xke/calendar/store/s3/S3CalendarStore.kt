package fr.xebia.api.xke.calendar.store.s3

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import fr.xebia.api.xke.calendar.CalendarEvent
import fr.xebia.api.xke.calendar.store.CalendarStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

class S3CalendarStore(private val bucketName: String,
                      private val key: String) : CalendarStore {

    private val s3 = AmazonS3ClientBuilder.defaultClient()

    private val objectMapper = ObjectMapper()

    override fun store(date: LocalDate, calendarEvents: List<CalendarEvent>) {

        val body = objectMapper.writeValueAsString(calendarEvents)

        val keyForDate = "$key/calendar-${date.format(ISO_LOCAL_DATE)}.json"

        s3.putObject(bucketName, keyForDate, body)
    }

}
