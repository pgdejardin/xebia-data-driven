package fr.xebia.user.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.StringInputStream
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import fr.xebia.user.extract.DirectoryUser
import fr.xebia.user.extract.store.UsersStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class S3UsersStore(private val amazonS3: AmazonS3,
                   private val bucketName: String,
                   private val bucketKey: String) : UsersStore {

    private val extractDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    private val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule()
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer()))

    override fun store(extractDate: LocalDate, directoryUsers: List<DirectoryUser>) {

        val extractFormat = extractDate.format(extractDateFormatter)

        val bucketContent = objectMapper.writeValueAsString(directoryUsers)

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = "application/json"
        objectMetadata.contentEncoding = Charsets.UTF_8.name()

        amazonS3.putObject(bucketName, "$bucketKey/$extractFormat/users.json", StringInputStream(bucketContent), objectMetadata)
    }

}

private class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeString(ISO_DATE_TIME.format(value))
    }
}
