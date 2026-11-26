package com.helloeva.integration.storage.application

interface FileStorage {
    fun upload(path: String, content: ByteArray, contentType: String): String
}
