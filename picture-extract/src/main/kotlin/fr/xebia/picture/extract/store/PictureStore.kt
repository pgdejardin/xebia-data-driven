package fr.xebia.picture.extract.store

import fr.xebia.picture.extract.Picture

interface PictureStore {

    fun store(hrs: Picture)

}
