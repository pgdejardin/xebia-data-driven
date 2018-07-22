package fr.xebia.picture.extract.store.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import fr.xebia.picture.extract.Picture
import fr.xebia.picture.extract.store.PictureStore
import java.io.ByteArrayInputStream

class S3PictureStore(private val mimeType: String,
                     private val amazonS3: AmazonS3,
                     private val bucketName: String,
                     private val bucketKey: String) : PictureStore {

    override fun store(picture: Picture) {

        val bucketKey = "$bucketKey/${picture.fileName}"

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = mimeType
        objectMetadata.contentLength = picture.content.size.toLong()

        amazonS3.putObject(bucketName, bucketKey, ByteArrayInputStream(picture.content), objectMetadata)
    }

}
