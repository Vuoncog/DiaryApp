package com.example.diaryapp.models

import com.example.diaryapp.utility.toRealmInstant
import io.realm.kotlin.ext.realmListOf;
import io.realm.kotlin.types.RealmInstant;
import io.realm.kotlin.types.RealmList;
import io.realm.kotlin.types.RealmObject;
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId;
import java.time.Instant

class Diary : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var ownerId: String = ""
    var title: String = ""
    var description: String = ""
    var mood: String = ""
    var images: RealmList<String> = realmListOf()
    var date: RealmInstant = Instant.now().toRealmInstant()
}
