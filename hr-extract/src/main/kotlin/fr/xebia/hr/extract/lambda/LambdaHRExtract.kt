package fr.xebia.hr.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import fr.xebia.hr.extract.HRExtract
import fr.xebia.hr.extract.source.google.GoogleSheetHRSource
import fr.xebia.hr.extract.source.google.credentials.GoogleCredentialSource
import fr.xebia.hr.extract.source.google.credentials.s3.S3GoogleCredentialSource
import fr.xebia.hr.extract.store.HRStore
import fr.xebia.hr.extract.store.s3.S3HRStore

class LambdaHRExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val hrSource = hrSource()
        val hrStore = hrStore()

        HRExtract(hrSource, hrStore).execute()
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

    private fun googleCredentialSource(): GoogleCredentialSource {

        val credentialBucket = "CREDENTIAL_BUCKET".env()
        val credentialKey = "CREDENTIAL_KEY".env()

        return S3GoogleCredentialSource(amazonS3, credentialBucket, credentialKey)
    }

    private fun hrSource(): GoogleSheetHRSource {

        val sheetId = "SHEET_ID".env()
        val sheetRange = "SHEET_RANGE".env()

        val googleCredentialSource = googleCredentialSource()

        return GoogleSheetHRSource(sheetId, sheetRange, googleCredentialSource.find())
    }

    private fun hrStore(): HRStore {

        val storeBucket = "STORE_BUCKET".env()
        val storeKey = "STORE_KEY".env()

        return S3HRStore(amazonS3, storeBucket, storeKey)
    }

}
