package fr.xebia.xke.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import fr.xebia.xke.extract.CalendarEvent
import fr.xebia.xke.extract.store.CalendarStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.time.format.DateTimeFormatter.ofPattern

class S3CalendarStore(private val amazonS3: AmazonS3,
                      private val bucketName: String,
                      private val bucketKey: String) : CalendarStore {

    private val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule()
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer()))

    private val extractDateFormatter = ofPattern("yyyy/MM/dd")
    private val calendarDateFormatter = ofPattern("yyyy-MM")

    override fun store(extractDate: LocalDate, calendarDate: LocalDate, calendarEvents: List<CalendarEvent>) {

        val extractFormat = extractDate.format(extractDateFormatter)
        val calendarFormat = calendarDate.format(calendarDateFormatter)

        val bucketContent = objectMapper.writeValueAsString(calendarEvents)

        amazonS3.putObject(bucketName, "$bucketKey/$extractFormat/$calendarFormat.json", bucketContent)
    }

}

private class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeString(ISO_DATE_TIME.format(value))
    }
}
