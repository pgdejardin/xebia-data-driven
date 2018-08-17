package fr.xebia.blog.extract

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import fr.xebia.blog.extract.source.BlogPostsSource
import fr.xebia.blog.extract.store.BlogPostsStore
import java.net.URL
import java.time.LocalDate

data class Post(
        val title: String,
        val author: Author,
        val link: URL,
        val terms: Terms,
        @JsonAlias("excerpt") val description: String
)

data class Author(
        val username: String,
        val first_name: String,
        val last_name: String,
        val avatar: String
)

data class Terms(@JsonProperty("category") val categories: List<Category>)

data class Category(val name: String, val slug: String)

class BlogPostExtract(private val blogPostsSource: BlogPostsSource, private val blogPostStore: BlogPostsStore) {
  fun extract(category: String) {
    val rawPosts = blogPostsSource.find(category)
    blogPostStore.store(category, rawPosts)
  }
}

operator fun LocalDate.rangeTo(end: LocalDate) = object : Iterable<LocalDate> {

  override fun iterator() = object : Iterator<LocalDate> {

    private var next = this@rangeTo.withDayOfMonth(1)

    override fun hasNext() = next <= end

    override fun next(): LocalDate {
      val current = next
      next = next.plusMonths(1)
      return current
    }
  }
}
