package fr.xebia.xke.extract.source.google

import com.amazonaws.util.StringInputStream
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes.CALENDAR_READONLY
import fr.xebia.xke.extract.CalendarEvent
import fr.xebia.xke.extract.source.CalendarSource
import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.LocalDateTime.ofInstant
import java.time.ZoneOffset.UTC
import java.util.*

class GoogleCalendarSource(private val calendarId: String,
                           private val serviceAccount: String) : CalendarSource {

    private val calendar by lazy(::getCalendarService)

    override fun find(begin: LocalDateTime, end: LocalDateTime): List<CalendarEvent> {

        return calendar.events()
            .list(calendarId)
            .setTimeMin(begin.toGoogleDateTime())
            .setTimeMax(end.toGoogleDateTime())
            .setSingleEvents(true)
            .setOrderBy("startTime")
            .execute()
            .items
            .map {
                CalendarEvent(
                    id = it.id ?: UUID.randomUUID().toString(),
                    startTime = it.start?.dateTime?.toLocalDateTime() ?: LocalDateTime.MAX,
                    endTime = it.end?.dateTime?.toLocalDateTime() ?: LocalDateTime.MAX,
                    summary = it.summary ?: "",
                    description = it.description ?: "",
                    attendees = it.attendees?.map { it.email } ?: emptyList()
                )
            }
    }

    private fun getCalendarService(): Calendar {

        val credential = GoogleCredential.fromStream(StringInputStream(serviceAccount))
            .createScoped(listOf(CALENDAR_READONLY))

        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory = JacksonFactory.getDefaultInstance()

        return Calendar.Builder(httpTransport, jacksonFactory, credential)
            .setApplicationName("xke-calendar-extract")
            .build()
    }

    private fun LocalDateTime.toGoogleDateTime() = DateTime(toInstant(UTC).toEpochMilli())

    private fun DateTime.toLocalDateTime(): LocalDateTime = ofInstant(ofEpochMilli(value), UTC)

}
