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

class LambdaCalendarExtract : RequestHandler<Map<String, String>?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: Map<String, String>?, context: Context) {

        val calendarSource = calendarSource()
        val calendarStore = calendarStore()

        val from = input?.get("extractBegin")?.let(LocalDate::parse) ?: LocalDate.now().withDayOfMonth(1).minusMonths(1)
        val end = input?.get("extractEnd")?.let(LocalDate::parse) ?: LocalDate.now().withDayOfMonth(1).plusMonths(1)

        val calendarExtract = CalendarExtract(calendarSource, calendarStore)

        calendarExtract.extract(from, end)
    }

    private fun calendarSource(): CalendarSource {

        val calendarIdKey = "CALENDAR_ID_KEY".env()
        val calendarIdRequest = GetParameterRequest().withName(calendarIdKey)
        val calendarId = amazonSSM.getParameter(calendarIdRequest).parameter.value

        val credentialKey = "CREDENTIAL_KEY".env()
        val credentialRequest = GetSecretValueRequest().withSecretId(credentialKey)
        val credential = amazonSecretManager.getSecretValue(credentialRequest).secretString

        return GoogleCalendarSource(calendarId, credential)
    }

    private fun calendarStore(): CalendarStore {

        val storeBucket = "STORE_BUCKET_NAME".env()
        val storeKey = "STORE_BUCKET_KEY".env()

        return S3CalendarStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
