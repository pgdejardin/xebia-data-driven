package fr.xebia.api.directory.users.source.google.credentials.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.util.StringInputStream
import org.apache.http.client.methods.HttpGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes


internal class S3GoogleCredentialSourceTest {

    private val bucketName = "s3-bucket"
    private val bucketKey = "directory-extract"

    private val amazonS3 = mock(AmazonS3::class.java)
    private val s3GoogleCredentialSource = S3GoogleCredentialSource(amazonS3, bucketName, bucketKey)

    @Test
    @DisplayName("object content should match stream returned for given bucket and key")
    fun testBucketObjectContent() {

        val expectedContentStream = S3ObjectInputStream(StringInputStream(""), HttpGet())
        val s3Object = mock(S3Object::class.java)
        given(s3Object.objectContent)
            .willReturn(expectedContentStream)
        given(amazonS3.getObject(bucketName, bucketKey))
            .willReturn(s3Object)

        val result = s3GoogleCredentialSource.find()

        assertEquals(expectedContentStream, result)
    }

    @Test
    @DisplayName("object key")
    fun testCredentials() {

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()


        // Setting the sub field with USER_EMAIL allows you to make API calls using the special keyword
        // "me" in place of a user id for that user.
        val credential = GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jacksonFactory)
                .setServiceAccountId("xdd-directory-api@xdd-directory-api.iam.gserviceaccount.com")
                .setServiceAccountScopes(listOf(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY))
                .setServiceAccountUser("akinsella@xebia.fr")
                .setServiceAccountPrivateKeyFromP12File(
                        java.io.File("/Users/bbousquet/Downloads/xdd-directory-api-a8765e3f4c06.p12"))
                .build()

        println("toto")
        Directory.Builder(httpTransport, jacksonFactory, credential)
                .setApplicationName("xdd-direcory-api")
                .build()
                .users()
                .list()
                .setDomain("xebia.fr")
                .setOrderBy("email")
                .execute()
                .users.forEach {
                    println(it)
                }

            assert(true)

    }
}
