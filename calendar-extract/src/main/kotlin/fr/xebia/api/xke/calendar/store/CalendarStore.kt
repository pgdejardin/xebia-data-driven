package fr.xebia.api.xke.calendar.store

import fr.xebia.api.xke.calendar.CalendarEvent
import java.time.LocalDate

interface CalendarStore {

    fun store(date: LocalDate, calendarEvents: List<CalendarEvent>)

}
