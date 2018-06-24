package fr.xebia.xke.extract

import fr.xebia.xke.extract.source.CalendarSource
import fr.xebia.xke.extract.store.CalendarStore
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.LocalDate

internal class CalendarExtractTest {

    private val calendarSource = mock(CalendarSource::class.java)

    private val calendarStore = mock(CalendarStore::class.java)

    private val calendarExtract = CalendarExtract(calendarSource, calendarStore)

    @Test
    @DisplayName("extraction should be stored in one month only when begin and end dates are in the same month")
    fun extractionInOneMonth() {

        val today = LocalDate.now()
        given(calendarSource.find(
            date(2018, 1, 1).atStartOfDay(),
            date(2018, 1, 31).atEndOfDay())
        ).willReturn(januaryEvents)

        calendarExtract.extract(date(2018, 1, 1), date(2018, 1, 2))

        verify(calendarStore).store(today, date(2018, 1, 1), januaryEvents)
        verifyNoMoreInteractions(calendarStore)
    }

    @Test
    @DisplayName("extraction should be stored in two months when begin and end dates are in consecutive months")
    fun extractionInTwoMonths() {

        val today = LocalDate.now()
        given(calendarSource.find(
            date(2018, 1, 1).atStartOfDay(),
            date(2018, 1, 31).atEndOfDay())
        ).willReturn(januaryEvents)

        given(calendarSource.find(
            date(2018, 2, 1).atStartOfDay(),
            date(2018, 2, 28).atEndOfDay())
        ).willReturn(februaryEvents)

        calendarExtract.extract(date(2018, 1, 1), date(2018, 2, 1))

        verify(calendarStore).store(today, date(2018, 1, 1), januaryEvents)
        verify(calendarStore).store(today, date(2018, 2, 1), februaryEvents)
        verifyNoMoreInteractions(calendarStore)
    }

    private fun date(year: Int, month: Int, day: Int) = LocalDate.of(year, month, day)

    private fun LocalDate.atEndOfDay() = atTime(java.time.LocalTime.MAX)

}
