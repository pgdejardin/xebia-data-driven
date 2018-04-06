package fr.xebia.api.xke.calendar.store

import fr.xebia.api.xke.calendar.CalendarEvent
import java.time.LocalDate

interface CalendarStore {

    fun store(extractDate: LocalDate = LocalDate.now(), calendarDate: LocalDate, calendarEvents: List<CalendarEvent>)

}
