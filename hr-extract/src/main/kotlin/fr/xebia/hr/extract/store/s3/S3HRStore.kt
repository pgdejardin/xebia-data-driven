package fr.xebia.hr.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import fr.xebia.hr.extract.HR
import fr.xebia.hr.extract.store.HRStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.format.DateTimeFormatter.ofPattern

class S3HRStore(private val amazonS3: AmazonS3,
                private val bucketName: String,
                private val bucketKey: String) : HRStore {

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule()
        .addSerializer(LocalDate::class.java, LocalDateSerializer()))

    private val extractDateFormatter = ofPattern("yyyy/MM/dd")

    override fun store(extractDate: LocalDate, hrs: List<HR>) {

        val extractFormat = extractDate.format(extractDateFormatter)

        val bucketContent = objectMapper.writeValueAsString(hrs)

        amazonS3.putObject(bucketName, "$bucketKey/$extractFormat/hr.json", bucketContent)
    }

}

private class LocalDateSerializer : JsonSerializer<LocalDate>() {
    override fun serialize(value: LocalDate, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeString(ISO_DATE.format(value))
    }
}
