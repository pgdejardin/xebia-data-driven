package fr.xebia.hr.extract.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import fr.xebia.hr.extract.HRExtract
import fr.xebia.hr.extract.source.google.GoogleSheetHRSource
import fr.xebia.hr.extract.store.HRStore
import fr.xebia.hr.extract.store.s3.S3HRStore

class LambdaHRExtract : RequestHandler<Any?, Unit> {

    private val amazonS3 by lazy(AmazonS3ClientBuilder::defaultClient)

    private val amazonSSM by lazy(AWSSimpleSystemsManagementClientBuilder::defaultClient)

    private val amazonSecretManager by lazy(AWSSecretsManagerClientBuilder::defaultClient)

    override fun handleRequest(input: Any?, context: Context) {

        val hrSource = hrSource()
        val hrStore = hrStore()

        HRExtract(hrSource, hrStore).execute()
    }

    private fun hrSource(): GoogleSheetHRSource {

        val serviceAccountKey = "SERVICE_ACCOUNT_KEY".env()
        val serviceAccountRequest = GetSecretValueRequest().withSecretId(serviceAccountKey)
        val serviceAccount = amazonSecretManager.getSecretValue(serviceAccountRequest).secretString

        val sheetIdKey = "SHEET_ID_KEY".env()
        val sheetIdRequest = GetParameterRequest().withName(sheetIdKey)
        val sheetId = amazonSSM.getParameter(sheetIdRequest).parameter.value

        val sheetRangeKey = "SHEET_RANGE_KEY".env()
        val sheetRangeRequest = GetParameterRequest().withName(sheetRangeKey)
        val sheetRange = amazonSSM.getParameter(sheetRangeRequest).parameter.value

        return GoogleSheetHRSource(sheetId, sheetRange, serviceAccount)
    }

    private fun hrStore(): HRStore {

        val storeBucket = "STORE_BUCKET_NAME".env()
        val storeKey = "STORE_BUCKET_KEY".env()

        return S3HRStore(amazonS3, storeBucket, storeKey)
    }

    private fun String.env() =
        System.getenv(this) ?: throw IllegalArgumentException("$this environment variable is not specified")

}
