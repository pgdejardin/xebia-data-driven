package fr.xebia.picture.extract.source.google.credentials

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential

interface GoogleCredentialSource {

    fun find(): GoogleCredential

}
