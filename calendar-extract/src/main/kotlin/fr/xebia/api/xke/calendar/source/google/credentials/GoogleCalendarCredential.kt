package fr.xebia.api.xke.calendar.source.google.credentials

import java.io.InputStream

interface GoogleCalendarCredential {

    fun find(): InputStream

}
