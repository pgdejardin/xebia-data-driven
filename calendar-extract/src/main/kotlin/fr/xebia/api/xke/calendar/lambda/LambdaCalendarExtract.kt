package fr.xebia.api.xke.calendar.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.api.xke.calendar.CalendarExtract
import fr.xebia.api.xke.calendar.source.CalendarSource
import fr.xebia.api.xke.calendar.source.google.GoogleCalendarSource
import fr.xebia.api.xke.calendar.source.google.credentials.GoogleCalendarCredential
import fr.xebia.api.xke.calendar.source.google.credentials.s3.S3GoogleCalendarCredential
import fr.xebia.api.xke.calendar.store.CalendarStore
import fr.xebia.api.xke.calendar.store.s3.S3CalendarStore
import java.time.LocalDate

class LambdaCalendarExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val calendarSource = calendarSource()
        val calendarStore = calendarStore()

        val from = "EXTRACT_BEGIN".env(LocalDate::parse) { LocalDate.now().withDayOfMonth(1) }
        val end = "EXTRACT_END".env(LocalDate::parse) { LocalDate.now().withDayOfMonth(1).plusMonths(1) }

        val calendarExtract = CalendarExtract(calendarSource, calendarStore)

        calendarExtract.extract(from, end)
    }

    private fun calendarSource(): CalendarSource {

        val calendarId = "CALENDAR_ID".env()
        val calendarCredentials = googleCalendarCredential()

        return GoogleCalendarSource(calendarId, calendarCredentials)
    }

    private fun googleCalendarCredential(): GoogleCalendarCredential {

        val credentialBucket = "CREDENTIAL_BUCKET".env()
        val credentialKey = "CREDENTIAL_KEY".env()

        return S3GoogleCalendarCredential(amazonS3, credentialBucket, credentialKey)
    }

    private fun calendarStore(): CalendarStore {

        val storeBucket = "STORE_BUCKET".env()
        val storeKey = "STORE_KEY".env()

        return S3CalendarStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

    private fun <T> String.env(convert: String.() -> T, default: () -> T) = System.getenv(this)?.convert() ?: default()

}
