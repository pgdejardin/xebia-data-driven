package fr.xebia.blog.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.StringInputStream
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import fr.xebia.blog.extract.Post
import fr.xebia.blog.extract.store.BlogPostsStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class S3BlogPostStore(private val amazonS3: AmazonS3, private val bucketName: String, private val bucketKey: String) : BlogPostsStore {

    private val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule()
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer()))

    private val extractDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val calendarDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    override fun store(extractDate: LocalDate, postsDate: LocalDate, blogPosts: List<Post>) {
        val extractFormat = extractDate.format(extractDateFormatter)
        val postsDateFormat = postsDate.format(calendarDateFormatter)

        val bucketContent = objectMapper.writeValueAsString(blogPosts)

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = "application/json"
        objectMetadata.contentEncoding = Charsets.UTF_8.name()

        amazonS3.putObject(bucketName, "$bucketKey/$extractFormat/$postsDateFormat.json", StringInputStream(bucketContent), objectMetadata)
    }
}

private class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeString(DateTimeFormatter.ISO_DATE_TIME.format(value))
    }
}
