package com.example.diaryapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.diaryapp.utility.Constant.IMAGE_TO_UPLOAD_TABLE

@Entity(
    tableName = IMAGE_TO_UPLOAD_TABLE,
)
data class ImageToUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val remoteImagePath: String,
    val sessionUri: String
)