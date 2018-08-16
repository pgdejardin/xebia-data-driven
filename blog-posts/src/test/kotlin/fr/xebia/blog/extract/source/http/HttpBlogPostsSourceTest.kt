package fr.xebia.blog.extract.source.http

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class HttpBlogPostsSourceTest {

  private val httpBlogPostsSource: HttpBlogPostsSource = HttpBlogPostsSource()

  @Test
  fun should_get_list_of_posts() {
    // Given
    val tag = "data"

    // When
    val posts = httpBlogPostsSource.find(tag)

    // Then
    println(posts)
    assert(posts.isNotEmpty())
    assertNotNull(posts.first())
  }
}
