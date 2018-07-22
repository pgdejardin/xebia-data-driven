package fr.xebia.picture.extract

import fr.xebia.picture.extract.queue.PictureQueue
import fr.xebia.picture.extract.source.PictureSource
import fr.xebia.picture.extract.store.PictureStore

class PictureRef(val id: String, val name: String)
class Picture(val content: ByteArray, val fileName: String)

class PictureExtract(private val pictureSource: PictureSource,
                     private val pictureQueue: PictureQueue) {

    fun extractToQueue() {

        pictureSource.findRefList()
            .forEach(pictureQueue::queue)
    }

}

class PictureStorage(private val pictureSource: PictureSource,
                     private val pictureStore: PictureStore) {

    fun storeFromQueue(pictureRefs: List<PictureRef>) {

        pictureRefs
            .map(pictureSource::find)
            .forEach(pictureStore::store)
    }

}
