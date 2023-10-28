package com.example.diaryapp.models

import io.realm.kotlin.ext.realmListOf;
import io.realm.kotlin.types.RealmInstant;
import io.realm.kotlin.types.RealmList;
import io.realm.kotlin.types.RealmObject;
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId;
class Diary : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var date: RealmInstant = RealmInstant.now()
    var description: String = ""
    var image: RealmList<String> = realmListOf()
    var mood: String = ""
    var ownerId: String = ""
    var title: String = ""
}
