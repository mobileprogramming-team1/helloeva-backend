package com.helloeva.integration.storage.infrastructure

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.helloeva.integration.storage.application.FileStorage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GcsFileStorage(
    @Value("\${gcp.project-id}")
    private val projectId: String,
    @Value("\${gcp.storage.bucket}")
    private val bucket: String,
) : FileStorage {

    private val storage: Storage by lazy {
        StorageOptions.newBuilder().setProjectId(projectId).build().service
    }

    override fun upload(path: String, content: ByteArray, contentType: String): String {
        val blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, path))
            .setContentType(contentType)
            .build()
        storage.create(blobInfo, content)
        return "https://storage.googleapis.com/$bucket/$path"
    }
}
