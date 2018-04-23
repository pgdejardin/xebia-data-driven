package fr.xebia.api.directory.users.source.google.credentials

import java.io.InputStream

interface GoogleCredentialSource {

    fun find(): InputStream

}
