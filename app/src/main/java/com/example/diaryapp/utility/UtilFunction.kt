package com.example.diaryapp.utility

import android.net.Uri
import androidx.core.net.toUri
import com.example.diaryapp.data.database.entity.ImageToDelete
import com.example.diaryapp.data.database.entity.ImageToUpload
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import io.realm.kotlin.types.RealmInstant
import java.time.Instant

fun RealmInstant.toInstant(): Instant {
    val sec = this.epochSeconds
    val nano = this.nanosecondsOfSecond
    return if (sec >= 0) {
        Instant.ofEpochSecond(sec, nano.toLong())
    } else {
        Instant.ofEpochSecond(sec - 1, 1_000_000 + nano.toLong())
    }
}

fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSecond
    val nano: Int = this.nano
    return if (sec >= 0) {
        RealmInstant.from(sec, nano)
    } else {
        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}

fun fetchImageFromDatabase(
    remoteImagePaths: List<String>,
    onImageDownload: (Uri) -> Unit,
    onReadyToDisplay: () -> Unit = {},
    onFailed: (Exception) -> Unit = {}
) {
    if (remoteImagePaths.isNotEmpty()) {
        val storage = FirebaseStorage.getInstance().reference
        remoteImagePaths.forEachIndexed { index, imagePath ->
            if (imagePath.trim().isNotEmpty()) {
                storage.child(imagePath.trim()).downloadUrl
                    .addOnSuccessListener {
                        onImageDownload(it)
//                        if (imagePath.lastIndexOf(remoteImagePaths.last()) == index) {
//                            onReadyToDisplay()
//                        }
                        onReadyToDisplay()
                    }
                    .addOnFailureListener {
                        onFailed(it)
                    }
            }
        }
    }
}

fun retryToUploadImagesToFirebase(
    imageToUpload: ImageToUpload,
    onSuccess: () -> Unit
) {
    val uploadTask = FirebaseStorage.getInstance().reference
    uploadTask.putFile(
        imageToUpload.imageUri.toUri(),
        storageMetadata { },
        imageToUpload.sessionUri.toUri()
    ).addOnSuccessListener {
        onSuccess()
    }
}

fun retryToDeleteImagesFromFirebase(
    imageToDelete: ImageToDelete,
    onSuccess: () -> Unit
) {
    val uploadTask = FirebaseStorage.getInstance().reference
    uploadTask.child(imageToDelete.remotePath).delete()
        .addOnSuccessListener {
            onSuccess()
        }
}
