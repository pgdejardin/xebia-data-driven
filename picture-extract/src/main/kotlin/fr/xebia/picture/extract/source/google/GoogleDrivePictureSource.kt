package fr.xebia.picture.extract.source.google

import com.amazonaws.util.StringInputStream
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import fr.xebia.picture.extract.Picture
import fr.xebia.picture.extract.PictureRef
import fr.xebia.picture.extract.source.PictureSource
import java.io.ByteArrayOutputStream

class GoogleDrivePictureSource(private val parentFolderId: String,
                               private val mimeType: String,
                               private val pageSize: Int,
                               private val serviceAccount: String) : PictureSource {

    private val drive by lazy {

        val jacksonFactory = JacksonFactory.getDefaultInstance()

        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val credential = GoogleCredential.fromStream(StringInputStream(serviceAccount))
            .createScoped(listOf(DriveScopes.DRIVE_READONLY))

        Drive.Builder(httpTransport, jacksonFactory, credential)
            .setApplicationName("picture-drive-extract")
            .build()
    }

    override fun findRefList(): List<PictureRef> {

        val fileList = drive
            .files()
            .list()
            .setSpaces("drive")
            .setOrderBy("name")
            .setPageSize(pageSize)
            .setQ("'$parentFolderId' in parents and mimeType = '$mimeType'")
            .execute()

        return fileList.files.map { PictureRef(it.id, it.name) }
    }

    override fun find(pictureRef: PictureRef): Picture {

        val byteArrayOutputStream = ByteArrayOutputStream()
        drive.files().get(pictureRef.id).executeMediaAndDownloadTo(byteArrayOutputStream)
        return Picture(byteArrayOutputStream.toByteArray(), pictureRef.name)
    }

}
