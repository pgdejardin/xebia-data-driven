package fr.xebia.blog.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.blog.extract.BlogPostExtract
import fr.xebia.blog.extract.source.http.HttpBlogPostsSource
import fr.xebia.blog.extract.store.BlogPostsStore
import fr.xebia.blog.extract.store.s3.S3BlogPostStore

class LambdaBlogPostsExtract : RequestHandler<String, Unit> {

  private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

  override fun handleRequest(category: String, context: Context?) {
    println("Starting blog post extraction category with $category")

    val blogPostsSource = HttpBlogPostsSource()
    val blogPostsStore = blogPostsStore()

    val blogPostsExtract = BlogPostExtract(blogPostsSource, blogPostsStore)

    blogPostsExtract.extract(category)

    println("Finished blog post extraction")
  }

  private fun blogPostsStore(): BlogPostsStore {
    val storeBucket = "STORE_BUCKET_NAME".env()
    val storeKey = "STORE_BUCKET_KEY".env()

    return S3BlogPostStore(amazonS3, storeBucket, storeKey)
  }

  private fun String.env() =
          System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")
}
