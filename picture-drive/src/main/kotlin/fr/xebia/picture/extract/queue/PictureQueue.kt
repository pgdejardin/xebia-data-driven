package fr.xebia.picture.extract.queue

import fr.xebia.picture.extract.PictureRef

interface PictureQueue {

    fun queue(pictureRef: PictureRef)

}
