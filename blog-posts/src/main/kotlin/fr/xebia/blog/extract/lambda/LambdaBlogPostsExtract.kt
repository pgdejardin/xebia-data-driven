package fr.xebia.blog.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import fr.xebia.blog.extract.source.BlogPostsSource
import fr.xebia.blog.extract.source.http.HttpBlogPostsSource
import fr.xebia.blog.extract.store.BlogPostsStore
import fr.xebia.blog.extract.store.s3.S3BlogPostStore
import java.time.LocalDate

class LambdaBlogPostsExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context?) {
        println("Starting post extraction with $input")

        val from = LocalDate.now().withDayOfMonth(1).minusMonths(1)
        val end = LocalDate.now().withDayOfMonth(1).plusMonths(1)

        println("Finished xke calendar extraction")
    }

    private fun blogPostsSource(): BlogPostsSource {
        return HttpBlogPostsSource()
    }

    private fun postsStore(): BlogPostsStore {
        val storeBucket = "STORE_BUCKET_NAME".env()
        val storeKey = "STORE_BUCKET_KEY".env()

        return S3BlogPostStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")
}
