package fr.xebia.xke.extract.source

import fr.xebia.xke.extract.CalendarEvent
import java.time.LocalDateTime

interface CalendarSource {

    fun find(begin: LocalDateTime, end: LocalDateTime): List<CalendarEvent>

}
