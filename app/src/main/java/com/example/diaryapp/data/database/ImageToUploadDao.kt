package com.example.diaryapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diaryapp.data.database.entity.ImageToUpload
import com.example.diaryapp.utility.Constant.IMAGE_TO_UPLOAD_TABLE

@Dao
interface ImageToUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUploadImage(imageToUpload: ImageToUpload)

    @Query("SELECT * FROM $IMAGE_TO_UPLOAD_TABLE ORDER BY id ASC")
    suspend fun getAllUploadImages(): List<ImageToUpload>

    @Query("DELETE FROM $IMAGE_TO_UPLOAD_TABLE WHERE id=:imageId")
    suspend fun cleanupImage(imageId: Int)
}