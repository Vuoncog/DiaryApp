package com.example.diaryapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diaryapp.data.database.entity.ImageToDelete
import com.example.diaryapp.data.database.entity.ImageToUpload
import com.example.diaryapp.utility.Constant

@Dao
interface ImageToDeleteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDeleteImage(imageToDelete: ImageToDelete)

    @Query("SELECT * FROM ${Constant.IMAGE_TO_DELETE_TABLE} ORDER BY id ASC")
    suspend fun getAllDeleteImages(): List<ImageToDelete>

    @Query("DELETE FROM ${Constant.IMAGE_TO_DELETE_TABLE} WHERE id=:imageId")
    suspend fun cleanupImage(imageId: Int)
}