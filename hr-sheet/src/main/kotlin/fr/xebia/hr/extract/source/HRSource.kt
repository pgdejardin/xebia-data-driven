package fr.xebia.hr.extract.source

import fr.xebia.hr.extract.HR

interface HRSource {

    fun find(): List<HR>

}
