package fr.xebia.xke.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import fr.xebia.xke.extract.CalendarExtract
import fr.xebia.xke.extract.source.CalendarSource
import fr.xebia.xke.extract.source.google.GoogleCalendarSource
import fr.xebia.xke.extract.store.CalendarStore
import fr.xebia.xke.extract.store.s3.S3CalendarStore
import java.time.LocalDate

class LambdaCalendarExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        println("Starting xke calendar extraction with $input")

        val calendarSource = calendarSource()
        val calendarStore = calendarStore()

        val from = LocalDate.now().withDayOfMonth(1).minusMonths(1)
        val end = LocalDate.now().withDayOfMonth(1).plusMonths(1)

        val calendarExtract = CalendarExtract(calendarSource, calendarStore)

        calendarExtract.extract(from, end)

        println("Finished xke calendar extraction")
    }

    private fun calendarSource(): CalendarSource {

        val calendarIdKey = "CALENDAR_ID_KEY".env()
        val calendarIdRequest = GetParameterRequest().withName(calendarIdKey)
        val calendarId = amazonSSM.getParameter(calendarIdRequest).parameter.value

        val serviceAccountKey = "SERVICE_ACCOUNT_KEY".env()
        val serviceAccountRequest = GetSecretValueRequest().withSecretId(serviceAccountKey)
        val serviceAccount = amazonSecretManager.getSecretValue(serviceAccountRequest).secretString

        return GoogleCalendarSource(calendarId, serviceAccount)
    }

    private fun calendarStore(): CalendarStore {

        val storeBucket = "STORE_BUCKET_NAME".env()
        val storeKey = "STORE_BUCKET_KEY".env()

        return S3CalendarStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
