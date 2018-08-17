package fr.xebia.blog.extract.source.http

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpGet
import fr.xebia.blog.extract.Post
import fr.xebia.blog.extract.source.BlogPostsSource

class HttpBlogPostsSource : BlogPostsSource {

  private val blogURL = "https://blog.xebia.fr"
  private val postsPathName = "wp-json/posts"

  private val mapper = jacksonObjectMapper()

  init {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  }

  override fun find(category: String, tag: String): List<Post> {
    val filterByTag = if (!tag.isBlank()) "&filter[tag]=$tag" else ""
    val queryParams = "?filter[category_name]=$category$filterByTag"
    val url = "$blogURL/$postsPathName$queryParams"

    url.httpGet().responseString().third.fold(
            { data -> return mapper.readValue(data, Array<Post>::class.java).toList() },
            { error ->
              println("An error of type ${error.exception} happened: ${error.message}")
              throw error
            })
  }

}
