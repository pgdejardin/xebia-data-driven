package fr.xebia.xke.extract

import fr.xebia.xke.extract.source.CalendarSource
import fr.xebia.xke.extract.store.CalendarStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

typealias Email = String

data class CalendarEvent(val id: String,
                         val startTime: LocalDateTime,
                         val endTime: LocalDateTime,
                         val summary: String,
                         val description: String,
                         val attendees: List<Email>)

class CalendarExtract(private val calendarSource: CalendarSource,
                      private val calendarStore: CalendarStore) {

    fun extract(begin: LocalDate, end: LocalDate) {

        val extractDate = LocalDate.now()

        (begin..end).forEach { monthBeginDate ->

            val monthBegin = monthBeginDate.atStartOfDay()
            val monthEnd = monthBeginDate.plusMonths(1).minusDays(1).atTime(LocalTime.MAX)

            val calendarEvents = calendarSource.find(monthBegin, monthEnd)

            calendarStore.store(extractDate, monthBeginDate, calendarEvents)
        }
    }
}

operator fun LocalDate.rangeTo(end: LocalDate) = object : Iterable<LocalDate> {

    override fun iterator() = object : Iterator<LocalDate> {

        private var next = this@rangeTo.withDayOfMonth(1)

        override fun hasNext() = next <= end

        override fun next(): LocalDate {
            val current = next
            next = next.plusMonths(1)
            return current
        }
    }
}
