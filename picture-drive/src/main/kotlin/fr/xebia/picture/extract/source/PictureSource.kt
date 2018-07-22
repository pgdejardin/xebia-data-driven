package fr.xebia.picture.extract.source

import fr.xebia.picture.extract.Picture
import fr.xebia.picture.extract.PictureRef

interface PictureSource {

    fun findRefList(): List<PictureRef>

    fun find(pictureRef: PictureRef): Picture

}
