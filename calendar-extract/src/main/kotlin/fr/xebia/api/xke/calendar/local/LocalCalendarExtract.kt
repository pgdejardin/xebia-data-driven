package fr.xebia.api.xke.calendar.local

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.api.xke.calendar.CalendarEvent
import fr.xebia.api.xke.calendar.CalendarExtract
import fr.xebia.api.xke.calendar.source.google.GoogleCalendarSource
import fr.xebia.api.xke.calendar.source.google.credentials.s3.S3GoogleCalendarCredential
import fr.xebia.api.xke.calendar.store.CalendarStore
import java.time.LocalDate

val amazonS3: AmazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

fun main(args: Array<String>) {

    val googleCalendarCredential = S3GoogleCalendarCredential(amazonS3, args[0], args[1])
    val calendarSource = GoogleCalendarSource(args[2], googleCalendarCredential)

    val calendarStore = ConsoleCalendarStore()

    val calendarExtract = CalendarExtract(calendarSource, calendarStore)

    val now = LocalDate.now()
    val beginDate = now.minusMonths(1)
    val endDate = now.plusMonths(1)

    calendarExtract.extract(beginDate, endDate)
}

class ConsoleCalendarStore : CalendarStore {

    override fun store(extractDate: LocalDate, calendarDate: LocalDate, calendarEvents: List<CalendarEvent>) {
        println("$extractDate - $calendarDate")
        calendarEvents.forEach(::println)
    }

}
