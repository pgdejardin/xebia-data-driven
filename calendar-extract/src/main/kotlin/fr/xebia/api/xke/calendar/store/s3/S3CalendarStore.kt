package fr.xebia.api.xke.calendar.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import fr.xebia.api.xke.calendar.CalendarEvent
import fr.xebia.api.xke.calendar.store.CalendarStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.time.format.DateTimeFormatter.ofPattern

class S3CalendarStore(private val amazonS3: AmazonS3,
                      private val bucketName: String,
                      private val key: String) : CalendarStore {

    private val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule()
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer()))

    private val extractDateFormatter = ofPattern("yyyy/MM/dd")
    private val calendarDateFormatter = ofPattern("yyyy-MM")

    override fun store(extractDate: LocalDate, calendarDate: LocalDate, calendarEvents: List<CalendarEvent>) {

        val bucketKey = "$key/${extractDate.format(extractDateFormatter)}/${calendarDate.format(calendarDateFormatter)}.json"

        val bucketContent = objectMapper.writeValueAsString(calendarEvents)

        amazonS3.putObject(bucketName, bucketKey, bucketContent)
    }

}

private class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeString(ISO_DATE_TIME.format(value))
    }
}
