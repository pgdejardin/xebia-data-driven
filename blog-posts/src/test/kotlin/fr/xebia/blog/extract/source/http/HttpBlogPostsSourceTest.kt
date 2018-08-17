package fr.xebia.blog.extract.source.http

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertAll

class HttpBlogPostsSourceTest {

  private val httpBlogPostsSource: HttpBlogPostsSource = HttpBlogPostsSource()

  @Test
  fun should_get_list_of_posts() {
    // Given
    val category = "nosql"

    // When
    val posts = httpBlogPostsSource.find(category)

    // Then
    assert(posts.isNotEmpty())
    assertNotNull(posts.first())
    assertAll("Post", {
      val post = posts.first()
      println(post)
      assertNotNull(post.author)
      assertNotNull(post.link) { "Link must not be blank" }
      assertNotNull(post.terms) { "Terms must not be empty" }
      assertNotNull(post.terms.categories.firstOrNull { it.name == "Data" }) {"Category $category should be in category list"}
    })
  }
}
