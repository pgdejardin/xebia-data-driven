package fr.xebia.api.xke.calendar.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import fr.xebia.api.xke.calendar.CalendarExtract
import fr.xebia.api.xke.calendar.source.CalendarSource
import fr.xebia.api.xke.calendar.source.google.GoogleCalendarSource
import fr.xebia.api.xke.calendar.source.google.credentials.GoogleCalendarCredential
import fr.xebia.api.xke.calendar.source.google.credentials.s3.S3GoogleCalendarCredential
import fr.xebia.api.xke.calendar.store.CalendarStore
import fr.xebia.api.xke.calendar.store.s3.S3CalendarStore
import java.time.LocalDate

class LambdaCalendarExtract : RequestHandler<String, Unit> {

    override fun handleRequest(input: String, context: Context) {

        val calendarSource = calendarSource()
        val calendarStore = calendarStore()

        val from = "EXTRACT_BEGIN".env(LocalDate::parse, LocalDate.now())
        val end = "EXTRACT_END".env(LocalDate::parse, LocalDate.now().plusMonths(1))

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

        return S3GoogleCalendarCredential(credentialBucket, credentialKey)
    }

    private fun calendarStore(): CalendarStore {

        val storeBucket = "STORE_BUCKET".env()
        val storeKey = "STORE_KEY".env()

        return S3CalendarStore(storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

    private fun <T> String.env(converter: (String) -> T, default: T) = converter(System.getenv(this)) ?: default

}
