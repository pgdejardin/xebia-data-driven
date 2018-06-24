package fr.xebia.xke.extract.store

import fr.xebia.xke.extract.CalendarEvent
import java.time.LocalDate

interface CalendarStore {

    fun store(extractDate: LocalDate = LocalDate.now(), calendarDate: LocalDate, calendarEvents: List<CalendarEvent>)

}
