package fr.xebia.api.xke.calendar.source

import fr.xebia.api.xke.calendar.CalendarEvent
import java.time.LocalDateTime

interface CalendarSource {

    fun find(begin: LocalDateTime, end: LocalDateTime): List<CalendarEvent>

}
