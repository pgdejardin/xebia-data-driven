package fr.xebia.api.xke.calendar.source.google

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.DateTime.parseRfc3339
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes.CALENDAR_READONLY
import fr.xebia.api.xke.calendar.CalendarEvent
import fr.xebia.api.xke.calendar.source.google.credentials.GoogleCalendarCredential
import fr.xebia.api.xke.calendar.source.CalendarSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

class GoogleCalendarSource(private val calendarId: String,
                           private val googleCalendarCredential: GoogleCalendarCredential) : CalendarSource {

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
            .map { CalendarEvent(it.summary ?: "", it.description ?: "") }
    }

    private fun getCalendarService(): Calendar {

        val inputStream = googleCalendarCredential.find()

        val credential = GoogleCredential.fromStream(inputStream)
            .createScoped(listOf(CALENDAR_READONLY))

        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

        return Calendar.Builder(httpTransport, jacksonFactory, credential)
            .setApplicationName("xdd@$calendarId")
            .build()
    }

    private fun LocalDateTime.toGoogleDateTime(): DateTime = parseRfc3339(format(ofPattern("yyyy-MM-dd'T'HH:mm:ss")))

}
