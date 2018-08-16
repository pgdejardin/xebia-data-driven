package fr.xebia.blog.extract

import com.fasterxml.jackson.annotation.JsonAlias
import java.net.URL
import java.time.LocalDate

data class Post(
        val title: String,
        val author: Author,
        @JsonAlias("excerpt") val description: String,
        val link: URL
//        val category: String,
//        val tags: List<String>
)

data class Author(
        val username: String,
        val first_name: String,
        val last_name: String,
        val avatar: String
)

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
