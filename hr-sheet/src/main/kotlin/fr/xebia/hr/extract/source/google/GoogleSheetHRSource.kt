package fr.xebia.hr.extract.source.google

import com.amazonaws.util.StringInputStream
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import fr.xebia.hr.extract.HR
import fr.xebia.hr.extract.source.HRSource
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoogleSheetHRSource(private val googleSheetId: String,
                          private val googleSheetRange: String,
                          private val serviceAccount: String) : HRSource {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val sheets by lazy(this::buildSheets)

    override fun find() = sheets.spreadsheets()
        .values()
        .batchGet(googleSheetId)
        .setRanges(listOf(googleSheetRange))
        .execute().valueRanges
        .flatMap { grid -> grid.getValues().map { it.toHR() } }

    private fun MutableList<Any>.toHR() = HR(
        this[0].toString(),
        this[1].toString(),
        this[2].toString().toLocalDate(),
        this[3].toString().toLocalDate(),
        this[4].toString()
    )

    private fun String.toLocalDate() = LocalDate.parse(this, dateTimeFormatter)

    private fun buildSheets(): Sheets {

        val jacksonFactory = JacksonFactory.getDefaultInstance()

        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val credential = GoogleCredential.fromStream(StringInputStream(serviceAccount))
            .createScoped(listOf(SheetsScopes.SPREADSHEETS_READONLY))

        return Sheets.Builder(httpTransport, jacksonFactory, credential)
            .setApplicationName("hr-sheet-extract")
            .build()
    }

}
