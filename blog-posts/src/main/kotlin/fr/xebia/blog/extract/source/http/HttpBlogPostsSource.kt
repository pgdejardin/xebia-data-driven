package fr.xebia.blog.extract.source.http

import awaitStringResponse
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpGet
import fr.xebia.blog.extract.Post
import fr.xebia.blog.extract.source.BlogPostsSource
import kotlinx.coroutines.experimental.runBlocking

class HttpBlogPostsSource : BlogPostsSource {

  private val blogURL = "https://blog.xebia.fr"
  private val postsPathName = "wp-json/posts"

  private val mapper = jacksonObjectMapper()

  init {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  }

  override fun find(tag: String?): List<Post> {
    val queryParams = "?filter=$tag"

    return runBlocking {
      val (request, response, result) = "$blogURL/$postsPathName$queryParams"
              .httpGet()
              .awaitStringResponse()

      result.fold({ data ->
        //                println(data)
        return@fold mapper.readValue(data, Array<Post>::class.java).toList()
//                return@fold data
      }, { error ->
        println("An error of type ${error.exception} happened: ${error.message}")
        throw error
      })
    }
  }

}
