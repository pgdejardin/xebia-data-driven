package fr.xebia.api.xke.calendar

import fr.xebia.api.xke.calendar.source.CalendarSource
import fr.xebia.api.xke.calendar.store.CalendarStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CalendarEvent(val summary: String,
                         val description: String)

class CalendarExtract(private val calendarSource: CalendarSource,
                      private val calendarStore: CalendarStore) {

    fun extract(begin: LocalDate, end: LocalDate) {

        for (localDate in begin..end) {

            val beginDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
            val endDateTime = LocalDateTime.of(localDate, LocalTime.MAX)

            val calendarEvents = calendarSource.find(beginDateTime, endDateTime)

            calendarStore.store(localDate, calendarEvents)
        }
    }
}

operator fun LocalDate.rangeTo(end: LocalDate) = object : Iterable<LocalDate> {

    override fun iterator() = object : Iterator<LocalDate> {

        private var next = this@rangeTo

        override fun hasNext() = next <= end

        override fun next(): LocalDate {
            val current = next
            next = next.plusMonths(1)
            return current
        }
    }
}
