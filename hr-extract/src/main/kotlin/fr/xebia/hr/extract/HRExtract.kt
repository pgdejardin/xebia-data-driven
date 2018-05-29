package fr.xebia.hr.extract

import fr.xebia.hr.extract.source.HRSource
import fr.xebia.hr.extract.store.HRStore
import java.time.LocalDate

data class HR(
    val lastName: String,
    val firstName: String,
    val xebiaStartDate: LocalDate,
    val careerStartDate: LocalDate
)

class HRExtract(private val hrSource: HRSource,
                private val hrStore: HRStore) {

    fun execute() {

        val hrExtracts = hrSource.find()

        hrStore.store(LocalDate.now(), hrExtracts)

    }

}
