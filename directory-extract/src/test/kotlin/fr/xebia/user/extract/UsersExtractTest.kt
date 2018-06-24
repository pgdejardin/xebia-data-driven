package fr.xebia.user.extract

import fr.xebia.user.extract.source.UsersSource
import fr.xebia.user.extract.store.UsersStore
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.LocalDate

internal class UsersExtractTest {

    private val usersSource = mock(UsersSource::class.java)

    private val usersStore = mock(UsersStore::class.java)

    private val usersExtract = UsersExtract(usersSource, usersStore)

    @Test
    @DisplayName("extraction should be stored in one month only when begin and end dates are in the same month")
    fun extraction() {

        // given
        val today = LocalDate.now()
        given(usersSource.find()).willReturn(users)

        // when
        usersExtract.extract()

        // then
        verify(usersStore).store(today, users)
        verifyNoMoreInteractions(usersStore)
    }

}
