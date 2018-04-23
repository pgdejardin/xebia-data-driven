package fr.xebia.api.directory.users

import fr.xebia.api.directory.users.source.UsersSource
import fr.xebia.api.directory.users.store.UsersStore
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*

internal class UsersExtractTest {

    private val usersSource = mock(UsersSource::class.java)

    private val usersStore = mock(UsersStore::class.java)

    private val usersExtract = UsersExtract(usersSource, usersStore)

    @Test
    @DisplayName("extraction should be stored in one month only when begin and end dates are in the same month")
    fun extraction() {

        given(usersSource.find("xebia.fr")).willReturn(users)

        usersExtract.extract("xebia.fr")

        verify(usersStore).store(users)
        verifyNoMoreInteractions(usersStore)
    }

}
