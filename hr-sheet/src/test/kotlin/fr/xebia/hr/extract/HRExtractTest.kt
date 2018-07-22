package fr.xebia.hr.extract

import fr.xebia.hr.extract.source.HRSource
import fr.xebia.hr.extract.store.HRStore
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.LocalDate

internal class HRExtractTest {

    private val hrSource = mock(HRSource::class.java)

    private val hrStore = mock(HRStore::class.java)

    private val hrExtract = HRExtract(hrSource, hrStore)

    @Test
    @DisplayName("extraction should find hr list and store it")
    fun extraction() {

        val today = LocalDate.now()
        given(hrSource.find()).willReturn(hrList)

        hrExtract.execute()

        verify(hrStore).store(today, hrList)
        verifyNoMoreInteractions(hrStore)
    }

}
