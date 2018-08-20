package fr.xebia.blog.extract

import fr.xebia.blog.extract.source.BlogPostsSource
import fr.xebia.blog.extract.store.BlogPostsStore
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoMoreInteractions
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

internal class PostExtractTest {

  private val blogPostsSource = mock(BlogPostsSource::class.java)

  private val blogPostStore = mock(BlogPostsStore::class.java)

  private val blogPostExtract = BlogPostExtract(blogPostsSource, blogPostStore)

  @Test
  @DisplayName("extraction should extract one time for the category given")
  fun postExtract() {
    // Given
    val category = "Data"
    given(blogPostsSource.find(category)).willReturn(posts)

    // When
    blogPostExtract.extract(category)

    // Then
    verify(blogPostStore).store(category, posts)
    verifyNoMoreInteractions(blogPostStore)
  }
}
