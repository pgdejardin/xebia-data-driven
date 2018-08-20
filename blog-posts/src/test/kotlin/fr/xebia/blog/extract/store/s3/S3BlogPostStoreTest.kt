package fr.xebia.blog.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import fr.xebia.blog.extract.posts
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class S3BlogPostStoreTest {

  private val bucketName = "s3-bucket"
  private val bucketKey = "blog-posts-extract"
  private val amazonS3 = Mockito.mock(AmazonS3::class.java)
  private val s3BlogPostStore = S3BlogPostStore(amazonS3, bucketName, bucketKey)

  @Test
  @DisplayName("bucket key should contain bucket key prefix, extract date and category name")
  fun testBucketKey() {
    // given
    val extractDate = LocalDate.now().format( DateTimeFormatter.ofPattern("yyyy/MM/dd"))

    // when
    s3BlogPostStore.store("Data", posts)

    // then
    verify(amazonS3).putObject(eq(bucketName), eq("$bucketKey/$extractDate/Data.json"), any(), any())
  }
}
