package fr.xebia.api.xke.calendar

import fr.xebia.api.xke.calendar.source.CalendarSource
import fr.xebia.api.xke.calendar.store.CalendarStore
import java.time.LocalDate
import java.time.LocalTime

data class CalendarEvent(val summary: String,
                         val description: String)

class CalendarExtract(private val calendarSource: CalendarSource,
                      private val calendarStore: CalendarStore) {

    fun extract(begin: LocalDate, end: LocalDate) = (begin..end).forEach { monthBeginDate ->

        val monthBegin = monthBeginDate.atStartOfDay()
        val monthEnd = monthBeginDate.plusMonths(1).minusDays(1).atTime(LocalTime.MAX)

        val calendarEvents = calendarSource.find(monthBegin, monthEnd)

        calendarStore.store(monthBeginDate, calendarEvents)
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
