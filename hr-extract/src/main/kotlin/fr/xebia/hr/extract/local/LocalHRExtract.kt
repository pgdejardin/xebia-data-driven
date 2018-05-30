package fr.xebia.hr.extract.local

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.hr.extract.HRExtract
import fr.xebia.hr.extract.source.google.GoogleSheetHRSource
import fr.xebia.hr.extract.source.google.credentials.s3.S3GoogleCredentialSource
import fr.xebia.hr.extract.store.s3.S3HRStore

fun main(args: Array<String>) {

    val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    val credentialBucketName = args[0]
    val credentialBucketKey = args[1]

    val storeBucketName = args[2]
    val storeBucketKey = args[3]

    val googleSheetId = args[4]
    val googleSheetRange = args[5]

    val googleCredentialSource = S3GoogleCredentialSource(amazonS3, credentialBucketName, credentialBucketKey)
    val googleCredential = googleCredentialSource.find()

    val hrSource = GoogleSheetHRSource(googleSheetId, googleSheetRange, googleCredential)

    val hrStore = S3HRStore(amazonS3, storeBucketName, storeBucketKey)

    HRExtract(hrSource, hrStore).execute()

}
