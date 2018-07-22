package fr.xebia.picture.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.picture.extract.Picture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.nio.charset.Charset

class S3PictureStoreTest {

    private val amazonS3 = mock(AmazonS3::class.java)

    private val mimeType = "image/jpeg"
    private val bucketName = "s3-bucket"
    private val bucketKey = "picture-drive"

    private val s3PictureStore = S3PictureStore(mimeType, amazonS3, bucketName, bucketKey)

    @Test
    @DisplayName("should store content with appropriate metadata")
    fun pictureStore() {

        // given
        val content = "Hello".toByteArray(Charset.defaultCharset())

        // when
        s3PictureStore.store(Picture(content, "some-picture.jpeg"))

        // then
        verify(amazonS3, only()).putObject(
            eq(bucketName),
            eq("$bucketKey/some-picture.jpeg"),
            argThat { content.contentEquals(it.readBytes()) },
            argThat { mimeType == it.contentType && content.size.toLong() == it.contentLength }
        )
    }

}
