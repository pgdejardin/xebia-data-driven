package fr.xebia.hr.extract.store

import fr.xebia.hr.extract.HR
import java.time.LocalDate

interface HRStore {

    fun store(extractDate: LocalDate, hrs: List<HR>)

}
