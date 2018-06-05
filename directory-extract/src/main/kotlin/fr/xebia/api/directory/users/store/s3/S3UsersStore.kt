package fr.xebia.api.directory.users.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import fr.xebia.api.directory.users.DirectoryUser
import fr.xebia.api.directory.users.store.UsersStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class S3UsersStore(private val amazonS3: AmazonS3,
                   private val bucketName: String,
                   private val key: String) : UsersStore {

    private val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule()
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer()))

    override fun store(directoryUsers: List<DirectoryUser>) {

        val bucketKey = "$key/users.json"

        val bucketContent = objectMapper.writeValueAsString(directoryUsers)

        amazonS3.putObject(bucketName, bucketKey, bucketContent)
    }

}

private class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeString(ISO_DATE_TIME.format(value))
    }
}
