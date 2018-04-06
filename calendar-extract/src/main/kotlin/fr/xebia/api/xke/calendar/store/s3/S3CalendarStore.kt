package fr.xebia.api.xke.calendar.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.databind.ObjectMapper
import fr.xebia.api.xke.calendar.CalendarEvent
import fr.xebia.api.xke.calendar.store.CalendarStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class S3CalendarStore(private val amazonS3: AmazonS3,
                      private val bucketName: String,
                      private val key: String) : CalendarStore {

    private val objectMapper = ObjectMapper()

    private val filePattern = DateTimeFormatter.ofPattern("yyyy-MM")

    override fun store(extractDate: LocalDate, calendarDate: LocalDate, calendarEvents: List<CalendarEvent>) {

        val body = objectMapper.writeValueAsString(calendarEvents)

        val keyForDate = "$key/${calendarDate.format(filePattern)}.json"

        amazonS3.putObject(bucketName, keyForDate, body)
    }

}
