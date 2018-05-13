package fr.xebia.api.xke.calendar

import java.time.LocalDateTime

val noEvents = emptyList<CalendarEvent>()

val attendees1 = listOf("mail1@test.com", "mail2@test.com")
val attendees2 = listOf("mail3@test.com", "mail4@test.com")

val januaryEvents = listOf(
    CalendarEvent("id1", LocalDateTime.of(2018, 1, 1, 10, 0), LocalDateTime.of(2018, 1, 1, 11, 0), "summary1", "description1", attendees1),
    CalendarEvent("id2", LocalDateTime.of(2018, 1, 1, 11, 0), LocalDateTime.of(2018, 1, 1, 12, 0), "summary2", "description2", attendees2)
)

val februaryEvents = listOf(
    CalendarEvent("id3", LocalDateTime.of(2018, 2, 1, 10, 0), LocalDateTime.of(2018, 2, 1, 11, 0), "summary3", "description3", attendees1),
    CalendarEvent("id4", LocalDateTime.of(2018, 2, 1, 11, 0), LocalDateTime.of(2018, 2, 1, 12, 0), "summary4", "description4", attendees2)
)
